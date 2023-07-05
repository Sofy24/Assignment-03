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
        log("got files => " + msg.files);
        computedFiles = new ArrayList<>();
        msg.getFiles().forEach(file -> computedFiles.add(ComputeFile.computeFile(file, msg.ranges)));
        //msg.worker.tell(new PingMsg(msg.count + 1, this.getSelf()), this.getSelf());
        log("finished");
    }

    private void log(String msg) {
        System.out.println("[" + this.getSelf().toString() + "] " + msg);
    }
}
