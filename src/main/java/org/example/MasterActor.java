package org.example;

import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MasterActor extends AbstractActor {
    private final List<FilePath> fileList = new ArrayList<>();
    private final static int NUMBER_OF_FILES = 50;
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(MessageProtocol.StartMessage.class, this::startSystem)
                .build();
    }

    private void startSystem(MessageProtocol.StartMessage message) {
        //start the system
        fileList.addAll(Objects.requireNonNull(FileSearcher.getAllFilesWithPaths(message.getDirectory())));

        //change behaviour for the rest
    }
}
