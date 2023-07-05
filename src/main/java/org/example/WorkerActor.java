package org.example;

import akka.actor.AbstractActor;



public class WorkerActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MessageProtocol.ReceiveFiles.class, this::onReceiveFilesMsg)
                .build();
    }

    private void onReceiveFilesMsg(MessageProtocol.ReceiveFiles msg) {
        log("got files => " + msg.files);
        //msg.worker.tell(new PingMsg(msg.count + 1, this.getSelf()), this.getSelf());
    }

    private void log(String msg) {
        System.out.println("[CounterUserActor] " + msg);
    }
}
