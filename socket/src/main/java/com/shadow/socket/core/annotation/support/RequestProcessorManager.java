package com.shadow.socket.core.annotation.support;

import com.shadow.socket.core.annotation.HandlerMethod;
import com.shadow.socket.core.annotation.RequestHandler;
import com.shadow.socket.core.annotation.RequestParam;
import com.shadow.socket.core.annotation.SessionAttr;
import com.shadow.socket.core.domain.Command;
import com.shadow.socket.core.domain.Request;
import com.shadow.socket.core.session.Session;
import com.shadow.util.lang.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
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

    private final Map<Command, RequestProcessor> requestProcessors = new HashMap<>();
    private static final RequestProcessor DEFAULT_PROCESSOR = new UnknownRequestProcessor();

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
        short module = typeAnnotation.module();
        Set<Method> handlerMethods = ReflectUtil.getDeclaredMethodsAnnotatedWith(bean.getClass(), HandlerMethod.class);
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
                if (p.isAnnotationPresent(com.shadow.socket.core.annotation.Session.class)) {
                    if (paramType != Session.class) {
                        throw new RuntimeException();
                    }
                    methodParameter = new SessionParameter(handlerMethod, paramType, p.getAnnotation(com.shadow.socket.core.annotation.Session.class));
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

            byte cmd = methodAnnotation.cmd();
            Command command = Command.valueOf(module, cmd);
            RequestProcessor preProcessor = requestProcessors.put(command, requestProcessor);
            if (preProcessor != null) {
                LOGGER.info("请求处理器被覆盖：" + command);
            }
        }
        return bean;
    }

    @Nonnull
    public RequestProcessor getProcessor(Request request) {
        return requestProcessors.getOrDefault(request.getCommand(), DEFAULT_PROCESSOR);
    }

    private static class UnknownRequestProcessor extends RequestProcessor {
        @Override
        public Object handle(@Nonnull Request request) throws InvocationTargetException, IllegalAccessException {
            LOGGER.error("No processor for " + request.getCommand());
            return null;
        }

        @Override
        public boolean isOmitResponse() {
            return true;
        }
    }
}
