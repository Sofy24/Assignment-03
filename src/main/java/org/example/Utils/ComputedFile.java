package org.example.Utils;

public class ComputedFile {

    final FilePath filePath;
    final Long minRange;
    final Long length;

    public ComputedFile(FilePath filePath, Long minRange, Long length) {
        this.filePath = filePath;
        this.minRange = minRange;
        this.length = length;
    }

    public FilePath getFilePath() {
        return filePath;
    }

    public Long getMinRange() {
        return minRange;
    }

    public Long getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "ComputedFile{" +
                "filePath=" + filePath +
                '}';
    }
}
