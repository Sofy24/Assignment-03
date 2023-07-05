package org.example;

public class LongRange {

    private final Long min;
    private final Long max;

    public LongRange(Long min, Long max) {
        this.min = min;
        this.max = max;
    }

    public boolean isInRange(Long value) {
        return value <= max && value >= min;
    }

    public Long getMin() {
        return min;
    }

    public Long getMax() {
        return max;
    }

    @Override
    public String toString() {
        return "LongRange{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}
