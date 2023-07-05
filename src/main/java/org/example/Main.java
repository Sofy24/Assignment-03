package org.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        String directory = "";
        int number_of_ranges = 5;
        int max_lines = 1000;
        int leaderboard = 10;
        final ActorSystem system = ActorSystem.create("actor-system");
        final ActorRef masterActor =  system.actorOf(Props.create(MasterActor.class), "master-actor");
        masterActor.tell(new MessageProtocol.StartMessage(directory, number_of_ranges, max_lines, leaderboard), null);
    }
}