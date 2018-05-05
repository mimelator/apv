package com.arranger.apv.msg;

import java.awt.Color;
import java.util.logging.Logger;

import com.arranger.apv.Main;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.frame.FrameFader;

public abstract class MessageSystem extends ShapeSystem {

	private static final Logger logger = Logger.getLogger(MessageSystem.class.getName());
	protected static final int DEFAULT_MESSAGE_DURATION_FRAMES = 100;
	
	
	private FadingMessage fadingMessage;
	
	protected abstract void _draw(FadingMessage fadingMessage); 
	protected abstract void onCreatedFadingMessage(FadingMessage fadingMessage);
	
	public MessageSystem(Main parent) {
		super(parent, null);
	}

	@Override
	public void setup() {
	}
	
	public void onNewMessage(String[] messages) {
		logger.fine(String.join(",", messages));
		fadingMessage = createFadingMessage(messages);
		onCreatedFadingMessage(fadingMessage);
	}

	protected FadingMessage createFadingMessage(String[] messages) {
		return new FadingMessage(new FrameFader(parent, DEFAULT_MESSAGE_DURATION_FRAMES), messages);
	}
	
	@Override
	public void draw() {
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
		
		new SafePainter(parent, () -> {
			_draw(fadingMessage);	
		}).paint();
		
	}
	
	@Override
	public void onFactoryUpdate() {
	
	}

	protected class FadingMessage {
		
		public FrameFader frameFader;
		public String [] messages;
		public SafePainter.LOCATION location;
		
		public FadingMessage(FrameFader frameFader, String [] message) {
			frameFader.startFade();
			this.frameFader = frameFader;
			this.messages = message;
		}
	}
	
	protected void doStandardFade(float pct) {
		doStandardFade(parent.getColor().getCurrentColor(), pct);
	}
	
	protected void doStandardFade(Color c, float pct) {
		float alpha = parent.lerpAlpha(pct);
		//parent.tint(c.getRGB(), alpha);  
		parent.fill(c.getRGB(), alpha);
	}
	
	protected String joinMessage(FadingMessage fadingMessage, String delimeter) {
		return String.join(delimeter, fadingMessage.messages);
	}
}
