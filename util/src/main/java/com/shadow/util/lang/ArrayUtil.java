package com.shadow.util.lang;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static com.shadow.util.lang.BitConverter.getBytes;

/**
 * @author nevermore on 2014/11/26
 */
public class ArrayUtil {

    public static void main(String[] args) {
        byte[] result = new byte[8];
        ArrayUtil.fill(Integer.MIN_VALUE, result, 4);
        System.out.println(Arrays.toString(result));
        System.out.println(BitConverter.toInt(org.apache.commons.lang3.ArrayUtils.subarray(result, 4, result.length)));
    }

    /**
     * 将int填充至指定数组的指定位置
     *
     * @param value
     * @param dest
     * @param offset
     */
    public static void fill(int value, byte[] dest, int offset) {
        fill(getBytes(value), dest, offset);
    }

    /**
     * @param src
     * @param dest
     * @param offset
     */
    public static void fill(@Nonnull byte[] src, @Nonnull byte[] dest, int offset) {
        if (dest.length < offset + src.length) {
            throw new ArrayIndexOutOfBoundsException("数组填充失败：目标数组长度不足");
        }
        for (final byte b : src) {
            dest[offset++] = b;
        }
    }
}
