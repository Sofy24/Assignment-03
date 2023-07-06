package part1.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Report {

    private final List<Pair<String, Long>> longestFiles;

    private final List<Pair<String, Double>> filesDistribution = new ArrayList<>();

    public Report(List<ComputedFile> files, List<LongRange> ranges, int longestFiles) {
        for (LongRange range : ranges) {
            filesDistribution.add(new Pair<>(range.toString(),
                    (double) files.stream().filter(f -> f.minRange.equals(range.getMin())).count() / files.size()));
        }
        this.longestFiles = files.stream()
                .sorted(Comparator.comparing(ComputedFile::getLength).reversed())
                .limit(longestFiles)
                .map(f -> new Pair<>(f.filePath.getCompleteFilePath(), f.getLength()))
                .collect(Collectors.toList());

    }

    public void getResults() {
        System.out.println("LONGEST FILES:");
        longestFiles.forEach(f -> System.out.println(f.getX() + " length: " + f.getY()));
        System.out.println("\n\nRANGES:");
        filesDistribution.forEach(r -> System.out.println(r.getX() + " " + r.getY() * 100 + "%"));
    }

}