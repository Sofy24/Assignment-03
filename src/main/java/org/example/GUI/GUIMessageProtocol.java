package org.example.GUI;

public class GUIMessageProtocol {

    static class startMessage {
        private final String directory;
        private final int longestFiles;
        private final int numberOfRanges;
        private final int maxLines;
        public startMessage(String directory, int longestFiles, int numberOfRanges, int maxLines) {
            this.directory = directory;
            this.longestFiles = longestFiles;
            this.numberOfRanges = numberOfRanges;
            this.maxLines = maxLines;
        }

        public String getDirectory() {
            return directory;
        }

        public int getLongestFiles() {
            return longestFiles;
        }

        public int getNumberOfRanges() {
            return numberOfRanges;
        }

        public int getMaxLines() {
            return maxLines;
        }
    }

    static class stopMessage {

    }

}
