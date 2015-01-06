package com.shadow.entity.proxy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.shadow.entity.IEntity;
import com.shadow.entity.annotation.AutoSave;
import com.shadow.entity.cache.EntityCache;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

/**
 * 实体类的代理类生成器
 *
 * @author nevermore on 2014/11/26.
 */
public class EntityProxyGenerator<PK extends Serializable, T extends IEntity<PK>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityProxyGenerator.class);

    private static final ClassPool CLASS_POOL = ClassPool.getDefault();
    private static final String CACHE_FIELD_NAME = "entityCache";
    private static final String ENTITY_FIELD_NAME = "entity";
    private final LoadingCache<Class<T>, Constructor<T>> constructorCache = CacheBuilder.newBuilder().concurrencyLevel(16).expireAfterAccess(30, TimeUnit.MINUTES).build(new ConstructorLoader());
    private final EntityCache<PK, T> entityCache;

    public EntityProxyGenerator(EntityCache<PK, T> entityCache) {
        this.entityCache = entityCache;
    }

    @SuppressWarnings("unchecked")
    public T generate(T entity) throws Exception {
        Class<T> entityClass = (Class<T>) entity.getClass();
        Constructor<T> constructor = constructorCache.getUnchecked(entityClass);
        return constructor.newInstance(entity, entityCache);
    }

    /**
     * 实体代理类的构造器的加载器，当缓存miss时自动加载
     */
    private class ConstructorLoader extends CacheLoader<Class<T>, Constructor<T>> {

        @SuppressWarnings("unchecked")
        @Override
        public Constructor<T> load(@Nonnull Class<T> entityClass) throws Exception {
            Class<T> entityProxyClass = createProxyClass(entityClass).toClass();
            return entityProxyClass.getConstructor(entityClass, EntityCache.class);
        }

        private CtClass createProxyClass(Class<? extends IEntity<?>> entityClass) throws Exception {
            CtClass proxyClass = CLASS_POOL.makeClass(proxyClassName(entityClass), getCtClass(entityClass));
            proxyClass.setInterfaces(getCtClasses(EntityProxy.class));

            addFields(proxyClass, entityClass);
            addConstructors(proxyClass, entityClass);
            addMethods(proxyClass, entityClass);
            return proxyClass;
        }

        /**
         * 添加属性
         *
         * @param proxyClass
         * @param entityClass
         * @throws Exception
         */
        private void addFields(CtClass proxyClass, Class<? extends IEntity<?>> entityClass) throws Exception {
            CtField entityField = new CtField(getCtClass(entityClass), ENTITY_FIELD_NAME, proxyClass);
            proxyClass.addField(entityField);

            CtField cacheServiceField = new CtField(getCtClass(EntityCache.class), CACHE_FIELD_NAME, proxyClass);
            proxyClass.addField(cacheServiceField);
        }

        /**
         * 添加构造器
         *
         * @param proxyClass
         * @param entityClass
         * @throws Exception
         */
        private void addConstructors(CtClass proxyClass, Class<?> entityClass) throws Exception {
            CtConstructor constructor = new CtConstructor(getCtClasses(entityClass, EntityCache.class), proxyClass);
            constructor.setModifiers(Modifier.PUBLIC);
            constructor.setBody("{" + "this." + ENTITY_FIELD_NAME + "=" + "$1;" + "this." + CACHE_FIELD_NAME + "=" + "$2;" + "}");
            proxyClass.addConstructor(constructor);
        }

        /**
         * 代理类添加方法
         *
         * @param proxyClass
         * @param entityClass
         * @throws Exception
         */
        private void addMethods(CtClass proxyClass, Class<? extends IEntity<?>> entityClass) throws Exception {
            ReflectionUtils.doWithMethods(entityClass, method -> {
                        try {
                            addMethod(proxyClass, method);
                        } catch (Exception e) {
                            LOGGER.error("实体代理类添加方法失败，method=" + method, e);
                            throw new RuntimeException(e);
                        }
                    }, method -> !(Modifier.isStatic(method.getModifiers()) || Modifier.isFinal(method.getModifiers())
                            || Modifier.isPrivate(method.getModifiers()) || method.isSynthetic() || method.getDeclaringClass() != entityClass)
            );

            // JsonIgnore注解
            ClassFile classFile = proxyClass.getClassFile();
            ConstPool constPool = classFile.getConstPool();
            AnnotationsAttribute annoAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
            Annotation annotation = new Annotation(JsonIgnore.class.getCanonicalName(), constPool);
            annoAttr.addAnnotation(annotation);

            // 添加EntityProxy接口的方法实现
            Method method = EntityProxy.class.getDeclaredMethods()[0];
            CtMethod entityProxyMethod = new CtMethod(getCtClass(method.getReturnType()), method.getName(), null, proxyClass);
            entityProxyMethod.getMethodInfo().addAttribute(annoAttr);// 添加JsonIgnore注解
            entityProxyMethod.setModifiers(Modifier.PUBLIC);
            entityProxyMethod.setBody("return this." + ENTITY_FIELD_NAME + ";");

            proxyClass.addMethod(entityProxyMethod);
        }

        private void addMethod(CtClass proxyClass, Method method) throws Exception {
            CtMethod ctMethod = new CtMethod(getCtClass(method.getReturnType()), method.getName(), getCtClasses(method.getParameterTypes()), proxyClass);
            ctMethod.setModifiers(method.getModifiers());
            ctMethod.setExceptionTypes(getCtClasses(method.getExceptionTypes()));

            StringJoiner body = new StringJoiner("", "{", "}");
            invokeEntityMethod(method, body);
            if (method.isAnnotationPresent(AutoSave.class)) {
                commitEntityUpdate(method, body);
            }
            returnInvokeResult(method, body);

            ctMethod.setBody(body.toString());
            proxyClass.addMethod(ctMethod);
        }

        private void invokeEntityMethod(Method method, StringJoiner body) {
            if (method.getReturnType() != Void.TYPE) {
                body.add(method.getReturnType().getCanonicalName()).add(" result = ");
            }
            body.add(ENTITY_FIELD_NAME).add(".").add(method.getName()).add("($$);");
        }

        private void commitEntityUpdate(Method method, StringJoiner body) {
            AutoSave autoSave = method.getAnnotation(AutoSave.class);
            String delimiter = "", prefix = "", suffix = "";
            if (method.getReturnType() != Void.TYPE && autoSave != null && !autoSave.forResult().isNull()) {
                prefix = "if (\"" + autoSave.forResult() + "\".equals(String.valueOf(result))) {";
                suffix = "}";
            }
            StringJoiner stringJoiner = new StringJoiner(delimiter, prefix, suffix);
            stringJoiner.add(CACHE_FIELD_NAME + "." + "update(entity);");
            body.add(stringJoiner.toString());
        }

        private void returnInvokeResult(Method method, StringJoiner body) {
            if (method.getReturnType() == Void.TYPE) {
                return;
            }
            body.add("return result;");
        }

        private String proxyClassName(Class<?> entityClass) {
            return entityClass.getCanonicalName() + "$PROXY";
        }

        private CtClass[] getCtClasses(Class<?>... classes) throws Exception {
            CtClass[] ctClasses = new CtClass[classes.length];
            for (int i = 0; i < classes.length; i++) {
                ctClasses[i] = getCtClass(classes[i]);
            }
            return ctClasses;
        }

        private CtClass getCtClass(Class<?> clazz) throws Exception {
            return CLASS_POOL.get(clazz.getCanonicalName());
        }
    }
}
