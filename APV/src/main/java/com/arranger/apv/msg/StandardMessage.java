package com.arranger.apv.msg;

import java.util.logging.Logger;

import com.arranger.apv.Main;
import com.arranger.apv.MessageSystem;
import com.arranger.apv.util.FrameFader;

public class StandardMessage extends MessageSystem {

	private static final Logger logger = Logger.getLogger(StandardMessage.class.getName());
	
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
	public void _draw(FadingMessage fadingMessage) {
		
		parent.strokeWeight(STROKE_WEIGHT);
		parent.textAlign(CENTER, CENTER);
		
		FrameFader frameFader = fadingMessage.frameFader;
		float fadePct = frameFader.getFadePct();
		logger.info("fadePct: " + fadePct);
		
		float tempTextSize = textSize + (sizeMultiplier / fadePct);
		parent.textSize(tempTextSize);
		
		doStandardFade(fadePct);
		
		printMessages(fadingMessage);
		parent.addSettingsMessage("  --textSize: " + textSize);
	}

	protected void printMessages(FadingMessage fadingMessage) {
		int width = parent.width / 2;
		
		String[] messages = fadingMessage.messages;
		int length = messages.length;
		
		//total compensation difference should be half the total change from the first to the last line
		float screenDividedByNumMessages = TEXT_OFFSET / (length * sizeMultiplier);
		float startingHeight = (parent.height / 2) - screenDividedByNumMessages;
		
		logger.fine(String.format("[sdbnm: %s] [sh: %s]", screenDividedByNumMessages, startingHeight));
		for (int index = 0; index < length; index++) {
			String msg = messages[index];
			
			float pct = (float)index / (float)length;
			float offset = startingHeight + (pct * TEXT_OFFSET);
			
			parent.pushMatrix();
			parent.translate(width, offset);
			parent.text(msg, 0, 0);
			parent.popMatrix();
			
			logger.fine(String.format("[index: %s] [msg: %s] [pct: %7] [offset: %s]", index, msg, pct, offset));
		}
	}
	
}
