package com.shadow.socket.core.annotation.support;

import com.shadow.common.util.lang.ReflectUtil;
import com.shadow.socket.core.annotation.*;
import com.shadow.socket.core.domain.Command;
import com.shadow.socket.core.domain.Request;
import com.shadow.socket.core.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author nevermore on 2014/11/26
 */
@Component
public final class RequestProcessorManager implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestProcessorManager.class);

    private final Map<Command, RequestProcessor> requestProcessors = new HashMap<>();
    private final Set<Command> commandsNotRequireIdentity = new HashSet<>();
    private static final RequestProcessor DEFAULT_PROCESSOR = new UnknownRequestProcessor();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RequestHandler typeAnnotation = ReflectUtil.getDeclaredAnnotation(bean.getClass(), RequestHandler.class);
        if (typeAnnotation == null) {
            return bean;
        }
        short module = typeAnnotation.module();
        Set<Method> handlerMethods = ReflectUtil.getDeclaredMethodsAnnotatedWith(bean.getClass(), HandlerMethod.class);
        for (Method handlerMethod : handlerMethods) {
            Parameter[] parameters = handlerMethod.getParameters();
            String[] paramNames = ReflectUtil.getParamNames(handlerMethod);
            HandlerMethod methodAnnotation = handlerMethod.getAnnotation(HandlerMethod.class);

            MethodParameter<?>[] methodParameters = new MethodParameter[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Parameter p = parameters[i];
                Class<?> paramType = p.getType();
                MethodParameter<?> methodParameter;
                if (paramType == Session.class) {
                    methodParameter = new SessionParameter();
                } else if (p.isAnnotationPresent(SessionAttr.class)) {
                    methodParameter = new SessionAttrParameter(p.getAnnotation(SessionAttr.class));
                } else if (p.isAnnotationPresent(RequestParam.class)) {
                    methodParameter = new RequestParameter(p.getAnnotation(RequestParam.class).value());
                } else {
                    methodParameter = new RequestParameter(paramNames[i]);
                }
                methodParameters[i] = methodParameter;
            }

            RequestProcessor requestProcessor = RequestProcessor.valueOf(handlerMethod, bean, methodParameters);

            byte cmd = methodAnnotation.cmd();
            Command command = Command.valueOf(module, cmd);
            RequestProcessor preProcessor = requestProcessors.put(command, requestProcessor);
            if (preProcessor != null) {
                LOGGER.error("请求处理器被覆盖：" + command);
            }

            if (!isIdentityRequired(handlerMethod)) {
                commandsNotRequireIdentity.add(command);
            }
        }
        return bean;
    }

    private boolean isIdentityRequired(Method method) {
        IdentityRequired identityRequired = method.getAnnotation(IdentityRequired.class);
        if (identityRequired == null) {
            identityRequired = method.getDeclaringClass().getAnnotation(IdentityRequired.class);
        }
        return identityRequired == null || identityRequired.value();
    }

    public boolean isIdentityRequired(Command command) {
        return !commandsNotRequireIdentity.contains(command);
    }

    @Nonnull
    public RequestProcessor getProcessor(Request request) {
        return requestProcessors.getOrDefault(request.getCommand(), DEFAULT_PROCESSOR);
    }

    private static class UnknownRequestProcessor extends RequestProcessor {
        @Override
        public Object handle(@Nonnull Request request) throws InvocationTargetException {
            LOGGER.error("No processor for " + request.getCommand());
            return null;
        }

        @Override
        public boolean isOmitResponse() {
            return true;
        }
    }
}
