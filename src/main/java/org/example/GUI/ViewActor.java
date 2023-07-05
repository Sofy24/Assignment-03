package org.example.GUI;
import akka.actor.AbstractActor;


public class ViewActor extends AbstractActor {
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(GUIMessageProtocol.startMessage.class, this::startSystem)
				.match(GUIMessageProtocol.stopMessage.class, this::stopSystem)
				.build();
	}

	private <P> void stopSystem(P p) {
	}

	private <P> void startSystem(P p) {

	}
}
