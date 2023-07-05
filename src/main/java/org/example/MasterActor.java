package org.example;

import akka.actor.AbstractActor;

public class MasterActor extends AbstractActor {

    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(MessageProtocol.StartMessage.class, this::startSystem)
                .build();
    }

    private void startSystem(MessageProtocol.StartMessage message) {
        //start the system
        message.getDirectory();
        //change behaviour for the rest
    }
}
