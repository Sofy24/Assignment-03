package org.example.GUI;

import akka.actor.ActorRef;
import org.example.Utils.ComputedFile;
import org.example.Utils.FilePath;
import org.example.Utils.LongRange;

import java.util.List;

public interface GUIMessageProtocol {

    class StartMessage {
        private final String directory;
        private final int longestFiles;
        private final int numberOfRanges;
        private final int maxLines;
        private final ViewFrame viewFrame;
        public StartMessage(String directory, int longestFiles, int numberOfRanges, int maxLines, ViewFrame viewFrame) {
            this.directory = directory;
            this.longestFiles = longestFiles;
            this.numberOfRanges = numberOfRanges;
            this.maxLines = maxLines;
            this.viewFrame = viewFrame;
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

        public ViewFrame getViewFrame() {
            return viewFrame;
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

    class ComputedFileMessage {
        private final ComputedFile computedFile;
        public final ActorRef replyTo;
        public ComputedFileMessage(ComputedFile computedFile, ActorRef replyTo) {
            this.computedFile = computedFile;
            this.replyTo = replyTo;
        }

        public ComputedFile getComputedFile() {
            return computedFile;
        }
    }

    class StopMessage {}

    class ContinueMessage {}

}
