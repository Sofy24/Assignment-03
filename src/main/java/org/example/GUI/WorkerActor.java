package org.example.GUI;

import akka.actor.AbstractActor;
import org.example.Utils.ComputeFile;
import org.example.Utils.ComputedFile;
import org.example.Utils.FilePath;
import org.example.Utils.LongRange;

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
        //compute file
        ComputedFile computedFile = ComputeFile.computeFile(files.remove(0), ranges);
        getContext().getParent().tell(new GUIMessageProtocol.ComputedFileMessage(computedFile, getSelf()), getSelf());
    }

    private void log(String msg) {
        System.out.println("[" + "worker" + "] " + msg);
    }
}
