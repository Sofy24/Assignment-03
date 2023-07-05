package org.example.CommandLine;

import akka.actor.AbstractActor;
import org.example.Utils.ComputeFile;
import org.example.Utils.ComputedFile;

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
        computedFiles = new ArrayList<>();
        msg.getFiles().forEach(file -> computedFiles.add(ComputeFile.computeFile(file, msg.getRanges())));
        //response and stop
        msg.replyTo.tell(new MessageProtocol.ComputedFilesMessage(computedFiles), this.getSelf());
    }

    private void log(String msg) {
        System.out.println("[" + "worker" + "] " + msg);
    }
}
