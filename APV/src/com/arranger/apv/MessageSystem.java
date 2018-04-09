package com.arranger.apv;

import java.awt.Color;
import java.util.logging.Logger;

import com.arranger.apv.util.FrameFader;

import processing.core.PApplet;

public abstract class MessageSystem extends ShapeSystem {

	private static final Logger logger = Logger.getLogger(MessageSystem.class.getName());
	protected static final int DEFAULT_MESSAGE_DURATION_FRAMES = 100;
	private static final int MAX_ALPHA = 255;
	
	private FadingMessage fadingMessage;
	
	protected abstract void _draw(FadingMessage fadingMessage); 
	
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
	
	@Override
	public void draw() {
		logger.fine("frame: " + parent.getFrameCount());
		parent.noStroke();
		
		if (fadingMessage == null) {
			return;
		} 
		
		FrameFader frameFader = fadingMessage.frameFader;
		String[] messages = fadingMessage.messages;
		String joinMessages = String.join(",", messages);
		if (!frameFader.isFadeActive()) {
			logger.info("finished transition for: " + joinMessages);
			fadingMessage = null;
			return;
		}
	
		parent.addSettingsMessage("  --messages: " + joinMessages);
		
		_draw(fadingMessage);
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
	
	protected float getAlapha(float pct) {
		float alpha = PApplet.lerp(0, MAX_ALPHA, pct);
		return alpha;
	}
	
	protected void doStandardFade(Color c, float pct) {
		float alpha = getAlapha(pct);
		parent.tint(c.getRGB(), alpha);  // Display at half opacity
	}
}
