package com.arranger.apv;

import java.util.logging.Logger;

import com.arranger.apv.util.FrameFader;

public abstract class MessageSystem extends ShapeSystem {

	private static final Logger logger = Logger.getLogger(MessageSystem.class.getName());
	
	protected static final int DEFAULT_MESSAGE_DURATION_FRAMES = 100;
	
	protected FadingMessage fadingMessage;
	
	
	public MessageSystem(Main parent) {
		super(parent, null);
	}

	@Override
	public void setup() {
	}
	
	public void onNewMessage(String[] messages) {
		logger.fine(String.join(",", messages));
		fadingMessage = new FadingMessage(new FrameFader(parent, DEFAULT_MESSAGE_DURATION_FRAMES), messages);
	}
	
	protected class FadingMessage {
		
		public FrameFader frameFader;
		public String [] messages;
		
		public FadingMessage(FrameFader frameFader, String [] message) {
			frameFader.startFade();
			this.frameFader = frameFader;
			this.messages = message;
		}
	}
}
