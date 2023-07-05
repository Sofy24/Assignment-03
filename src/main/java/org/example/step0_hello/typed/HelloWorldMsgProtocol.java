package org.example.step0_hello.typed;

public class HelloWorldMsgProtocol {

	/* messages types */
	
	public static class SayHello {
		
		private final String content;

		public SayHello(String content) {
			this.content = content;
		}
		
		public String getContent() {
			return content;
		}
	}


}
