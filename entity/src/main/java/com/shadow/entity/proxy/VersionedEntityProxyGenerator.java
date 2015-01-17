package com.shadow.entity.proxy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shadow.entity.IEntity;
import com.shadow.entity.cache.EntityCache;
import com.shadow.entity.cache.RegionEntityCache;
import com.shadow.entity.cache.annotation.AutoSave;
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
import java.util.concurrent.atomic.AtomicLong;

/**
 * 实体类的代理类生成器
 *
 * @author nevermore on 2014/11/26.
 */
public class VersionedEntityProxyGenerator<K extends Serializable, V extends IEntity<K>> implements EntityProxyGenerator<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionedEntityProxyGenerator.class);

    private static final ClassPool CLASS_POOL = ClassPool.getDefault();
    private static final String CACHE_FIELD_NAME = "entityCache";
    private static final String ENTITY_FIELD_NAME = "entity";
    private static final String EDIT_VERSION_FIELD_NAME = "editVersion";
    private static final String DB_VERSION_FIELD_NAME = "dbVersion";
    private final Constructor<V> constructor;
    private final EntityCache<K, V> entityCache;

    public VersionedEntityProxyGenerator(EntityCache<K, V> entityCache, Class<V> entityClass) {
        this.entityCache = entityCache;
        this.constructor = getConstructor(entityClass);
    }

    @Nonnull
    public V generate(V entity) {
        try {
            return constructor.newInstance(entity, entityCache);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Constructor<V> getConstructor(@Nonnull Class<V> entityClass) {
        try {
            Class<V> entityProxyClass = createProxyClass(entityClass).toClass();
            return entityProxyClass.getConstructor(entityClass, EntityCache.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private CtClass createProxyClass(Class<? extends IEntity<?>> entityClass) throws Exception {
        CtClass proxyClass = CLASS_POOL.makeClass(proxyClassName(entityClass), getCtClass(entityClass));
        proxyClass.setInterfaces(getCtClasses(VersionedEntityProxy.class));

        addFields(proxyClass, entityClass);
        addConstructors(proxyClass, entityClass);
        addMethods(proxyClass, entityClass);

        proxyClass.writeFile("G:/");
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
        entityField.setModifiers(Modifier.PRIVATE);
        proxyClass.addField(entityField);

        CtField cacheField = new CtField(getCtClass(EntityCache.class), CACHE_FIELD_NAME, proxyClass);
        cacheField.setModifiers(Modifier.PRIVATE);
        proxyClass.addField(cacheField);

        CtField editVersionField = new CtField(getCtClass(AtomicLong.class), EDIT_VERSION_FIELD_NAME, proxyClass);
        editVersionField.setModifiers(Modifier.PRIVATE);
        proxyClass.addField(editVersionField);

        CtField dbVersionField = new CtField(getCtClass(long.class), DB_VERSION_FIELD_NAME, proxyClass);
        dbVersionField.setModifiers(Modifier.PRIVATE | Modifier.VOLATILE);
        proxyClass.addField(dbVersionField);
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
        constructor.setBody(
                "{" + "this." + ENTITY_FIELD_NAME + "=" + "$1;" +
                        "this." + CACHE_FIELD_NAME + "=" + "$2;" +
                        "this." + EDIT_VERSION_FIELD_NAME + "= new " + AtomicLong.class.getCanonicalName() + "();" +
                        "}");
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
        CtMethod getEntity = new CtMethod(getCtClass(IEntity.class), "getEntity", null, proxyClass);
        getEntity.getMethodInfo().addAttribute(annoAttr);// 添加JsonIgnore注解
        getEntity.setModifiers(Modifier.PUBLIC);
        getEntity.setBody("return this." + ENTITY_FIELD_NAME + ";");
        proxyClass.addMethod(getEntity);

        // 添加VersionedEntityProxy接口的方法实现
        CtMethod postEdit = new CtMethod(getCtClass(long.class), "postEdit", null, proxyClass);
        postEdit.setModifiers(Modifier.PUBLIC);
        postEdit.setBody("return this." + EDIT_VERSION_FIELD_NAME + ".incrementAndGet();");
        proxyClass.addMethod(postEdit);

        CtMethod isPersisted = new CtMethod(getCtClass(boolean.class), "isPersisted", null, proxyClass);
        isPersisted.setModifiers(Modifier.PUBLIC);
        isPersisted.setBody("return this." + DB_VERSION_FIELD_NAME + " >= " + "this." + EDIT_VERSION_FIELD_NAME + ".get();");
        proxyClass.addMethod(isPersisted);

        CtMethod updateDbVersion = new CtMethod(getCtClass(void.class), "postPersist", null, proxyClass);
        updateDbVersion.setModifiers(Modifier.PUBLIC);
        updateDbVersion.setBody("this." + DB_VERSION_FIELD_NAME + " = " + "this." + EDIT_VERSION_FIELD_NAME + ".get();");
        proxyClass.addMethod(updateDbVersion);
    }

    private void addMethod(CtClass proxyClass, Method method) throws Exception {
        CtMethod ctMethod = new CtMethod(getCtClass(method.getReturnType()), method.getName(), getCtClasses(method.getParameterTypes()), proxyClass);
        ctMethod.setModifiers(method.getModifiers());
        ctMethod.setExceptionTypes(getCtClasses(method.getExceptionTypes()));

        StringJoiner body = new StringJoiner("", "{", "}");

        keepOldIndexValue(method, body);

        invokeEntityMethod(method, body);

        commitEntityUpdate(method, body);

        returnInvokeResult(method, body);

        ctMethod.setBody(body.toString());
        proxyClass.addMethod(ctMethod);
    }

    private void keepOldIndexValue(Method method, StringJoiner body) {
        AutoSave autoSave = method.getAnnotation(AutoSave.class);
        if (autoSave == null || !autoSave.withIndexValueChanged() || !(entityCache instanceof RegionEntityCache)) {
            return;
        }
        body.add("Object oldValue = ((").add(RegionEntityCache.class.getCanonicalName()).add(")")
                .add(CACHE_FIELD_NAME).add(").getIndexValue(").add(ENTITY_FIELD_NAME).add(");");
    }

    private void invokeEntityMethod(Method method, StringJoiner body) {
        if (method.getReturnType() != Void.TYPE) {
            body.add(method.getReturnType().getCanonicalName()).add(" result = ");
        }
        body.add(ENTITY_FIELD_NAME).add(".").add(method.getName()).add("($$);");
    }

    private void commitEntityUpdate(Method method, StringJoiner body) {
        AutoSave autoSave = method.getAnnotation(AutoSave.class);
        if (autoSave == null) {
            return;
        }
        String delimiter = "", prefix = "", suffix = "";
        if (method.getReturnType() != Void.TYPE && !autoSave.forResult().isNull()) {
            prefix = "if (\"" + autoSave.forResult() + "\".equals(String.valueOf(result))) {";
            suffix = "}";
        }
        StringJoiner stringJoiner = new StringJoiner(delimiter, prefix, suffix);
        if (autoSave.withIndexValueChanged() && entityCache instanceof RegionEntityCache) {
            stringJoiner.add("((").add(RegionEntityCache.class.getCanonicalName()).add(")").add(CACHE_FIELD_NAME).add(").updateWithIndexValueChanged(entity, oldValue);");
        } else {
            stringJoiner.add(CACHE_FIELD_NAME).add(".update(entity);");
        }
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
