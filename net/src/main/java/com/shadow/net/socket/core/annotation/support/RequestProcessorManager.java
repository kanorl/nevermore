package com.shadow.net.socket.core.annotation.support;

import com.shadow.net.socket.core.annotation.*;
import com.shadow.net.socket.core.domain.Command;
import com.shadow.util.lang.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author nevermore on 2014/11/26
 */
@Component
public final class RequestProcessorManager implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestProcessorManager.class);

    private static final Map<Command, RequestProcessor> REQUEST_PROCESSOR_MAP = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RequestHandler typeAnnotation = ReflectUtil.getDeclaredAnnotation(bean.getClass(), RequestHandler.class);
        if (typeAnnotation == null) {
            return bean;
        }
        int module = typeAnnotation.module();
        Set<Method> handlerMethods = ReflectUtil.getDeclaredMethodsAnnotatedBy(bean.getClass(), HandlerMethod.class);
        for (Method handlerMethod : handlerMethods) {
            ReflectionUtils.makeAccessible(handlerMethod);

            Parameter[] parameters = handlerMethod.getParameters();
            String[] paramNames = ReflectUtil.getParamNames(handlerMethod);
            HandlerMethod methodAnnotation = handlerMethod.getAnnotation(HandlerMethod.class);

            MethodParameter[] methodParameters = new MethodParameter[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Parameter p = parameters[i];
                Class<?> paramType = p.getType();
                MethodParameter methodParameter;
                if (p.isAnnotationPresent(Session.class)) {
                    if (paramType != com.shadow.net.socket.core.session.Session.class) {
                        throw new RuntimeException();
                    }
                    methodParameter = new SessionParameter(handlerMethod, paramType, p.getAnnotation(Session.class));
                } else if (p.isAnnotationPresent(SessionAttr.class)) {
                    methodParameter = new SessionAttrParameter(handlerMethod, paramType, p.getAnnotation(SessionAttr.class));
                } else if (p.isAnnotationPresent(RequestParam.class)) {
                    methodParameter = new RequestParameter(handlerMethod, paramType, p.getAnnotation(RequestParam.class).value());
                } else {
                    methodParameter = new RequestParameter(handlerMethod, paramType, paramNames[i]);
                }
                methodParameters[i] = methodParameter;
            }

            RequestProcessor requestProcessor = RequestProcessor.valueOf(handlerMethod, bean, methodParameters);

            int cmd = methodAnnotation.cmd();
            Command command = Command.valueOf(module, cmd);
            RequestProcessor preProcessor = REQUEST_PROCESSOR_MAP.put(command, requestProcessor);
            if (preProcessor != null) {
                LOGGER.info("请求处理器被覆盖：" + command);
            }
        }
        return bean;
    }

    public static RequestProcessor getRequestProcessor(Command command) {
        RequestProcessor processor = REQUEST_PROCESSOR_MAP.get(command);
        return processor == null ? processor : processor;
    }
}
