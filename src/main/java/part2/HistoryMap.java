package part2;

import part1.Utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class HistoryMap {
    private Map<String, Integer> wrappedMap;

    public HistoryMap() {
        wrappedMap = new HashMap<>();
    }

    public Map<String, Integer> getWrappedMap() {
        return wrappedMap;
    }

    public void setWrappedMap(Map<String, Integer> wrappedMap) {
        this.wrappedMap = wrappedMap;
    }

}
