package org.example.CommandLine;

import akka.actor.ActorRef;
import org.example.Utils.ComputedFile;
import org.example.Utils.FilePath;
import org.example.Utils.LongRange;

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
        private final List<FilePath> files;
        private final List<LongRange> ranges;

        public final ActorRef replyTo;
        public ReceiveFilesMessage(List<FilePath> files, List<LongRange> ranges, ActorRef replyTo) {
            this.files = files;
            this.ranges = ranges;
            this.replyTo = replyTo;
        }

        public List<FilePath> getFiles() {
            return files;
        }

        public List<LongRange> getRanges() {
            return ranges;
        }
    }

    class ComputedFilesMessage {
        private final List<ComputedFile> computedFiles;

        public ComputedFilesMessage(List<ComputedFile> computedFiles) {
            this.computedFiles = computedFiles;
        }

        public List<ComputedFile> getComputedFiles() {
            return computedFiles;
        }
    }
}
