package part1.Utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class ComputeFile {

    public static ComputedFile computeFile(FilePath filePath, List<LongRange> ranges) {
        return computeFile(filePath.getCompleteFilePath(), ranges);
    }

    public static ComputedFile computeFile(String filePath, List<LongRange> ranges) {
        //process a file into a ComputedFile
        long fileLen;
        //number of lines
        try (Stream<String> lines = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            fileLen = lines.count();
        } catch (IOException e) {
            return null;
        }
        //assign correct range
        for (LongRange range : ranges) {
            if (range.isInRange(fileLen)) {
                return new ComputedFile(new FilePath(filePath), range.getMin(), fileLen);
            }
        }
        return null;
    }
}