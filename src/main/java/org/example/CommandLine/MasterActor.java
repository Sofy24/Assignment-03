package org.example.CommandLine;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.example.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MasterActor extends AbstractActor {
    private List<FilePath> fileList;
    private List<ActorRef> workers;
    private List<ComputedFile> computedFiles;
    private List<LongRange> ranges;
    private int leaderboard;
    private final static int FILES_PER_ACTOR = 50;
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(MessageProtocol.StartMessage.class, this::startSystem)
                .match(MessageProtocol.ComputedFilesMessage.class, this::receiveComputedFiles)
                .build();
    }



    private void startSystem(MessageProtocol.StartMessage message) {
        //start the system
        //get all files
        fileList = new ArrayList<>(Objects.requireNonNull(FileSearcher.getAllFilesWithPaths(message.getDirectory())));
        workers = new ArrayList<>();
        computedFiles = new ArrayList<>();
        leaderboard = message.getLeaderboard();
        int numberOfFiles = fileList.size();
        ranges = CreateRange.generateRanges(message.getMaxLines() , message.getNumberOfRanges());
        //change context
        for (int i = 0; i <= numberOfFiles / FILES_PER_ACTOR; i++) {
            ActorRef worker = this.getContext().actorOf(Props.create(WorkerActor.class), "worker-" + i);
            worker.tell(new MessageProtocol.ReceiveFilesMessage(fileList.subList(i * FILES_PER_ACTOR,
                    Math.min((i + 1) * FILES_PER_ACTOR, numberOfFiles)), ranges, this.getSelf()), this.getSelf());
            workers.add(worker);
            getContext().watch(worker);
        }
    }

    private void receiveComputedFiles(MessageProtocol.ComputedFilesMessage message) {
        computedFiles.addAll(message.getComputedFiles());
        workers.remove(0);
        if (workers.isEmpty()) {
            Report report = new Report(computedFiles, ranges, leaderboard);
            report.getResults();
            System.exit(0);
        }
    }
}
