package com.shadow.util.codec;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * @author nevermore on 2015/1/1.
 */
@SuppressWarnings("unchecked")
public class ProtobufCodec {

    public static <T> T decode(@Nonnull byte[] data, Class<T> type) {
        requireNonNull(data);
        requireNonNull(type);

        Schema<T> schema = RuntimeSchema.getSchema(type);
        T obj = schema.newMessage();
        ProtobufIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }

    @Nonnull
    public static <T> byte[] encode(T obj) {
        if (obj == null) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }

        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        Class<T> clazz = (Class<T>) obj.getClass();
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        return ProtobufIOUtil.toByteArray(obj, schema, buffer);
    }
}
