package com.shadow.common.util.codec;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @author nevermore on 2014/11/27
 */
@SuppressWarnings("unchecked")
public class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeFactory TYPE_FACTORY = TypeFactory.defaultInstance();

    static {
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setVisibilityChecker(
                        OBJECT_MAPPER.getVisibilityChecker()
                                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                );
    }

    public static String toJson(Object o) {
        try {
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            LOGGER.error("json转换失败: json={}, class={}", json, clazz.getName());
            throw new RuntimeException(e);
        }
    }

    public static <E, T extends Collection<E>> T toCollection(String json, Class<T> clazz, Class<E> elementType) {
        CollectionType collectionType = TYPE_FACTORY.constructCollectionType(clazz, elementType);
        try {
            return OBJECT_MAPPER.readValue(json, collectionType);
        } catch (Exception e) {
            LOGGER.error("json转换失败: json={}, class={}, elementType={} ", json, clazz.getName(), elementType.getName());
            throw new RuntimeException(e);
        }
    }

    public static <E> Set<E> toSet(String json, Class<E> elementType) {
        return toCollection(json, HashSet.class, elementType);
    }

    public static <E> List<E> toList(String json, Class<E> elementType) {
        return toCollection(json, ArrayList.class, elementType);
    }

    public static <K, V> Map<K, V> toMap(String json, Class<K> keyType, Class<V> valueType) {
        return toMap(json, HashMap.class, keyType, valueType);
    }

    public static <K, V, T extends Map<K, V>> T toMap(String json, Class<T> type, Class<K> keyType, Class<V> valueType) {
        MapType mapType = TYPE_FACTORY.constructMapType(type, keyType, valueType);
        try {
            return OBJECT_MAPPER.readValue(json, mapType);
        } catch (IOException e) {
            LOGGER.error("json转换失败: json={}, class={}", json, type.getName());
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (IOException e) {
            LOGGER.error("json转换失败: json={}, class={}.", json, type.getType().getTypeName());
            throw new RuntimeException(e);
        }
    }
}
