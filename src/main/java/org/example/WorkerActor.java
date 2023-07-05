package org.example;

import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.List;


public class WorkerActor extends AbstractActor {

    private List<ComputedFile> computedFiles;
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MessageProtocol.ReceiveFilesMessage.class, this::onReceiveFilesMsg)
                .build();
    }

    private void onReceiveFilesMsg(MessageProtocol.ReceiveFilesMessage msg) {
        log("got files => " + msg.getFiles());
        computedFiles = new ArrayList<>();
        msg.getFiles().forEach(file -> computedFiles.add(ComputeFile.computeFile(file, msg.getRanges())));
        //msg.worker.tell(new PingMsg(msg.count + 1, this.getSelf()), this.getSelf());
        log("finished");
        //response and stop
        msg.replyTo.tell(new MessageProtocol.ComputedFilesMessage(computedFiles), this.getSelf());
        getContext().stop(this.getSelf());
    }

    private void log(String msg) {
        System.out.println("[" + "worker" + "] " + msg);
    }
}
