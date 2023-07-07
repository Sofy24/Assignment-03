package part2;

import java.util.HashMap;
import java.util.Map;

public class HistoryMap {
    private Map<Integer, Map<Integer, Integer>> coloredPixels;

    public HistoryMap() {
        coloredPixels = new HashMap<>();
    }

    public Map<Integer, Map<Integer, Integer>> getColoredPixels() {
        return coloredPixels;
    }

    public void setColoredPixels(Map<Integer, Map<Integer, Integer>> coloredPixels) {
        this.coloredPixels = coloredPixels;
    }
}
