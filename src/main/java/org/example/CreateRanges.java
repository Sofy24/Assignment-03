package org.example;

import java.util.ArrayList;
import java.util.List;

 class CreateRange {

    public static List<LongRange> generateRanges(int maxLines, int numberOfRanges) {
        List<LongRange> ranges = new ArrayList<>();
        final int rangeSize = maxLines / (numberOfRanges - 1);
        for (long i = 0; i < numberOfRanges; i++) {
            ranges.add(new LongRange(
                    rangeSize * i, i != (numberOfRanges - 1) ? rangeSize * (i + 1) - 1 : Long.MAX_VALUE));
        }
        return ranges;
    }
}