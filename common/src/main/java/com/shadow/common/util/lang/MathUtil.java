package com.shadow.common.util.lang;

/**
 * @author nevermore on 2014/11/26
 */
public final class MathUtil {

    public static final int MAXIMUM_POW_OF_2 = 1 << 30;

    /**
     * 返回与其最接近的2次方正数
     *
     * @param x
     * @return a positive power of 2.
     * @see java.util.HashMap#tableSizeFor(int)
     */
    public static int ensurePowerOf2(int x) {
        int n = x - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_POW_OF_2) ? MAXIMUM_POW_OF_2 : n + 1;
    }
}
