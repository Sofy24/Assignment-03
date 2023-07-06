package part1.Utils;

import java.util.Objects;

public class FilePath {

    private final String path;
    private final String fileName;

    public FilePath(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    public FilePath(String fullPath) {
        this.path = fullPath.substring(0, fullPath.lastIndexOf(System.getProperty("file.separator")));
        this.fileName = fullPath.substring(fullPath.lastIndexOf(System.getProperty("file.separator")) + 1);
    }

    public String getCompleteFilePath() {
        return path + System.getProperty("file.separator") + fileName;
    }

    @Override
    public String toString() {
        return "FilePath{" +
                "path='" + path + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilePath filePath = (FilePath) o;
        return Objects.equals(path, filePath.path) && Objects.equals(fileName, filePath.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, fileName);
    }

}
