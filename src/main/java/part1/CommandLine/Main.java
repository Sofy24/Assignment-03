package part1.CommandLine;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Main {
    public static void main(String[] args) {
        String directory = args[0];
        int longestFiles = Integer.parseInt(args[1]);
        int numberOfRanges = Integer.parseInt(args[2]);
        int maxLines = Integer.parseInt(args[3]);
        final ActorSystem system = ActorSystem.create("actor-system");
        final ActorRef masterActor =  system.actorOf(Props.create(MasterActor.class), "master-actor");
        masterActor.tell(new MessageProtocol.StartMessage(directory, numberOfRanges, maxLines, longestFiles), null);
    }
}