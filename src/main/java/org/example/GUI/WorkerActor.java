package org.example.GUI;

import akka.actor.AbstractActor;
import org.example.ComputeFile;
import org.example.ComputedFile;
import org.example.FilePath;
import org.example.LongRange;

import java.util.ArrayList;
import java.util.List;


public class WorkerActor extends AbstractActor {

    private List<FilePath> files;
    private List<LongRange> ranges;
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GUIMessageProtocol.ReceiveFilesMessage.class, this::onReceiveFilesMsg)
                .match(GUIMessageProtocol.ContinueMessage.class, this::computeFile)
                .build();
    }

    private void onReceiveFilesMsg(GUIMessageProtocol.ReceiveFilesMessage msg) {
        files = new ArrayList<>(msg.getFiles());
        ranges = msg.getRanges();
        getSelf().tell(new GUIMessageProtocol.ContinueMessage(), getSelf());
    }

    private void computeFile(GUIMessageProtocol.ContinueMessage msg){
        if (files.isEmpty()) {
            return;
        }
        ComputedFile computedFile = ComputeFile.computeFile(files.remove(0), ranges);
        getContext().getParent().tell(new GUIMessageProtocol.ComputedFileMessage(computedFile, getSelf()), getSelf());
        //computedFiles = new ArrayList<>();
        //msg.getFiles().forEach(file -> computedFiles.add(ComputeFile.computeFile(file, msg.getRanges())));
        //response and stop
        //msg.replyTo.tell(new MessageProtocol.ComputedFilesMessage(computedFiles), this.getSelf());
    }

    private void log(String msg) {
        System.out.println("[" + "worker" + "] " + msg);
    }
}
