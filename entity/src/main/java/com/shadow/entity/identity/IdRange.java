package com.shadow.entity.identity;

/**
 * @author nevermore on 2015/1/10
 */
public class IdRange {
    private final long min;
    private final long max;

    public IdRange(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    @Override
    public String toString() {
        return "min=" + min + ", max=" + max;
    }
}
