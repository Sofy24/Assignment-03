package org.example;

public class MessageProtocol {

    public static class StartMessage {

        private final String directory;
        private final int number_of_ranges;
        private final int max_lines;
        private final int leaderboard;


        public StartMessage(String directory, int number_of_ranges, int max_lines, int leaderboard) {
            this.directory = directory;
            this.number_of_ranges = number_of_ranges;
            this.max_lines = max_lines;
            this.leaderboard = leaderboard;
        }

        public String getDirectory() {
            return directory;
        }

        public int getNumber_of_ranges() {
            return number_of_ranges;
        }

        public int getMax_lines() {
            return max_lines;
        }

        public int getLeaderboard() {
            return leaderboard;
        }
    }
}
