package org.example.GUI;

import akka.actor.ActorRef;
import org.example.FilePath;
import org.example.LongRange;

import java.util.List;

public interface GUIMessageProtocol {

    class StartMessage {
        private final String directory;
        private final int longestFiles;
        private final int numberOfRanges;
        private final int maxLines;
        public StartMessage(String directory, int longestFiles, int numberOfRanges, int maxLines) {
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

    class StopMessage {}

    class ContinueMessage {}

}
