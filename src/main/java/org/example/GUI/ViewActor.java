package org.example.GUI;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.example.*;
import org.example.CommandLine.MessageProtocol;
import org.example.CommandLine.WorkerActor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ViewActor extends AbstractActor {

	private boolean stopFlag = false;
	private List<FilePath> fileList;
	private List<ActorRef> workers;
	private List<ComputedFile> computedFiles;
	private List<LongRange> ranges;
	private int longestFiles;
	private final static int FILES_PER_ACTOR = 50;
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(GUIMessageProtocol.StartMessage.class, this::startSystem)
				.match(GUIMessageProtocol.StopMessage.class, this::stopSystem)
				.build();
	}

	private Receive handleWorkersBehaviour() {
		return receiveBuilder()
				.match(GUIMessageProtocol.StartMessage.class, this::restartSystem)
				.match(GUIMessageProtocol.StopMessage.class, this::stopSystem)
				.build();
	}



	private void startSystem(GUIMessageProtocol.StartMessage message) {
		//start the system
		//get all files
		fileList = new ArrayList<>(Objects.requireNonNull(FileSearcher.getAllFilesWithPaths(message.getDirectory())));
		workers = new ArrayList<>();
		computedFiles = new ArrayList<>();
		longestFiles = message.getLongestFiles();
		int numberOfFiles = fileList.size();
		ranges = CreateRange.generateRanges(message.getMaxLines() , message.getNumberOfRanges());
		//change context
		for (int i = 0; i <= numberOfFiles / FILES_PER_ACTOR; i++) {
			ActorRef worker = this.getContext().actorOf(Props.create(WorkerActor.class), "worker-" + i);
			worker.tell(new MessageProtocol.ReceiveFilesMessage(fileList.subList(i * FILES_PER_ACTOR,
					Math.min((i + 1) * FILES_PER_ACTOR, numberOfFiles)), ranges, this.getSelf()), this.getSelf());
			workers.add(worker);
			getContext().watch(worker);
		}
		//change behaviour to handle interaction with workers and stopFlag
		this.getContext().become(handleWorkersBehaviour());
	}



	private void stopSystem(GUIMessageProtocol.StopMessage message) {
		stopFlag = true;
	}

	private void restartSystem(GUIMessageProtocol.StartMessage message) {
		stopFlag = false;
		workers.forEach(worker -> worker.tell(new GUIMessageProtocol.ContinueMessage(), getSelf()));
	}

}
