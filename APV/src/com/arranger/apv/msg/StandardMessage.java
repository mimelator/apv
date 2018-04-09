package com.arranger.apv.msg;

import java.awt.Color;
import java.util.logging.Logger;

import com.arranger.apv.Main;
import com.arranger.apv.MessageSystem;
import com.arranger.apv.util.FrameFader;

import processing.core.PApplet;

public class StandardMessage extends MessageSystem {

	private static final Logger logger = Logger.getLogger(StandardMessage.class.getName());
	
	private static final int MAX_ALPHA = 255;
	private static final int START_TEXT_SIZE = 50;
	private static final int STROKE_WEIGHT = 5;
	private static final int TEXT_OFFSET = 200;
	
	private int textSize;
	private float sizeMultiplier = 3.5f;
	
	public StandardMessage(Main parent) {
		super(parent);
	}

	@Override
	public void setup() {
		super.setup();
		textSize = START_TEXT_SIZE;
	}
	
	@Override
	public void draw() {
		logger.fine("frame: " + parent.getFrameCount());
		
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
		
		parent.strokeWeight(STROKE_WEIGHT);
		parent.textAlign(CENTER, CENTER);
		
		float fadePct = frameFader.isFadeNew() ? 1 : frameFader.getFadePct();
		logger.info("fadePct: " + fadePct);
		
		float tempTextSize = textSize + (sizeMultiplier / fadePct);
		parent.textSize(tempTextSize);
		
		Color textColor = parent.getColorSystem().getCurrentColor();
		float alpha = PApplet.lerp(0, MAX_ALPHA, 1 - fadePct);
		parent.fill(textColor.getRGB(), alpha);
		
		printMessages();
		
		parent.addSettingsMessage("  --messages: " + joinMessages);
		parent.addSettingsMessage("  --textSize: " + textSize);
	}

	protected void printMessages() {
		int width = parent.width / 2;
		
		String[] messages = fadingMessage.messages;
		int length = messages.length;
		
		//total compensation difference should be half the total change from the first to the last line
		float screenDividedByNumMessages = TEXT_OFFSET / (length * sizeMultiplier);
		float startingHeight = (parent.height / 2) - screenDividedByNumMessages;
		
		logger.fine(String.format("[sdbnm: %1s] [sh: %2s]", screenDividedByNumMessages, startingHeight));
		for (int index = 0; index < length; index++) {
			String msg = messages[index];
			
			float pct = (float)index / (float)length;
			float offset = startingHeight + (pct * TEXT_OFFSET);
			
			parent.pushMatrix();
			parent.translate(width, offset);
			parent.text(msg, 0, 0);
			parent.popMatrix();
			
			logger.fine(String.format("[index: %1s] [msg: %2s] [pct: %3s] [offset: %4s]", index, msg, pct, offset));
		}
	}
	
}
