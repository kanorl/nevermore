package com.shadow.util.lang;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.shadow.util.lang.BitConverter.getBytes;
import static org.slf4j.helpers.MessageFormatter.format;

/**
 * @author nevermore on 2014/11/26
 */
public final class ArrayUtil {

    public static void main(String[] args) {
        byte[] result = new byte[8];
        ArrayUtil.fillInt(Integer.MIN_VALUE, result, 4);
        System.out.println(Arrays.toString(result));
        System.out.println(BitConverter.toInt(org.apache.commons.lang3.ArrayUtils.subarray(result, 4, result.length)));
    }

    /**
     * 将int填充至指定数组的指定位置
     *
     * @param value  填充内容
     * @param dest   目标数组
     * @param offset 目标数组偏移量
     */
    public static void fillInt(int value, byte[] dest, int offset) {
        fill(getBytes(value), dest, offset);
    }

    public static void fillShort(short value, byte[] dest, int offset) {
        fill(getBytes(value), dest, offset);
    }

    public static void fillByte(byte value, byte[] dest, int offset) {
        fill(getBytes(value), dest, offset);
    }

    /**
     * @param src    填充内容
     * @param dest   目标数组
     * @param offset 目标数组偏移量
     */
    public static void fill(@Nonnull byte[] src, @Nonnull byte[] dest, int offset) {
        checkNotNull(src);
        checkNotNull(dest);

        if (offset < 0 || dest.length < offset + src.length) {
            throw new ArrayIndexOutOfBoundsException(format("数组填充失败：目标数组长度不足，length={}，offset={}", dest.length, offset).getMessage());
        }

        for (byte b : src) {
            dest[offset++] = b;
        }
    }
}
