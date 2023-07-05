package org.example;

import java.util.List;

public interface MessageProtocol {

    class StartMessage {

        private final String directory;
        private final int numberOfRanges;
        private final int maxLines;
        private final int leaderboard;


        public StartMessage(String directory, int number_of_ranges, int max_lines, int leaderboard) {
            this.directory = directory;
            this.numberOfRanges = number_of_ranges;
            this.maxLines = max_lines;
            this.leaderboard = leaderboard;
        }

        public String getDirectory() {
            return directory;
        }

        public int getNumberOfRanges() {
            return numberOfRanges;
        }

        public int getMaxLines() {
            return maxLines;
        }

        public int getLeaderboard() {
            return leaderboard;
        }
    }

    class ReceiveFilesMessage {
        public final List<FilePath> files;
        public final List<LongRange> ranges;
        public ReceiveFilesMessage(List<FilePath> files, List<LongRange> ranges) {
            this.files = files;
            this.ranges = ranges;
        }

        public List<FilePath> getFiles() {
            return files;
        }

        public List<LongRange> getRanges() {
            return ranges;
        }
    }
}
