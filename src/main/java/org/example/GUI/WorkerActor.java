package org.example.GUI;

import akka.actor.AbstractActor;
import org.example.CommandLine.MessageProtocol;
import org.example.ComputeFile;
import org.example.ComputedFile;
import org.example.FilePath;

import java.util.ArrayList;
import java.util.List;


public class WorkerActor extends AbstractActor {

    private List<FilePath> files;
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GUIMessageProtocol.ReceiveFilesMessage.class, this::onReceiveFilesMsg)
                .build();
    }

    private void onReceiveFilesMsg(GUIMessageProtocol.ReceiveFilesMessage msg) {
        files = new ArrayList<>(msg.getFiles());
        getSelf().tell(new GUIMessageProtocol.ContinueMessage(), getSelf());
    }

    private void computeFile(GUIMessageProtocol.ContinueMessage msg){
        //computedFiles = new ArrayList<>();
        //msg.getFiles().forEach(file -> computedFiles.add(ComputeFile.computeFile(file, msg.getRanges())));
        //response and stop
        //msg.replyTo.tell(new MessageProtocol.ComputedFilesMessage(computedFiles), this.getSelf());
    }

    private void log(String msg) {
        System.out.println("[" + "worker" + "] " + msg);
    }
}
