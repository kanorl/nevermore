package com.shadow.entity.identity;

/**
 * @author nevermore on 2015/1/10
 */
public class Range {
    private final long min;
    private final long max;

    private Range(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public static Range valueOf(long min, long max) {
        return new Range(min, max);
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public boolean isOutOfRange(long id) {
        return id < min || id > max;
    }

    @Override
    public String toString() {
        return "[min=" + min + ", max=" + max + "]";
    }
}
