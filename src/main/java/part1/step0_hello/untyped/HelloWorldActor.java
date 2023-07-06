package part1.step0_hello.untyped;

import akka.actor.AbstractActor;

public class HelloWorldActor extends AbstractActor {

	private int helloCounter;
	
	/* configure message handlers */
	
	public Receive createReceive() {
		return receiveBuilder()
				.match(HelloWorldMsgProtocol.SayHello.class, this::onSayHello)
	            .build();
	}
	
	private void onSayHello(HelloWorldMsgProtocol.SayHello msg) {
 	   helloCounter++;
 	   System.out.println("Hello " + msg.getContent() + " from " + this.getContext().getSelf() + " - count " + helloCounter);
		
	}
}