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
	
	private int textSize;
	private float sizeMultiplier = 3.5f;
	
	public StandardMessage(Main parent) {
		super(parent);
	}

	@Override
	public void setup() {
		super.setup();
		textSize = 50;
	}
	
	@Override
	public void draw() {
		logger.fine("frame: " + parent.getFrameCount());
		
		if (fadingMessage == null) {
			return;
		} 
		
		FrameFader frameFader = fadingMessage.frameFader;
		if (!frameFader.isFadeActive()) {
			logger.info("finished transition for: " + fadingMessage.message);
			fadingMessage = null;
			return;
		}
		
		parent.textAlign(CENTER, CENTER);
		parent.translate(parent.width / 2, parent.height / 2);
		
		float fadePct = frameFader.isFadeNew() ? 1 : frameFader.getFadePct();
		logger.info("fadePct: " + fadePct);
		
		float tempTextSize = textSize + (sizeMultiplier / fadePct);
		parent.textSize(tempTextSize);
		
		Color textColor = parent.getColorSystem().getCurrentColor();
		float alpha = PApplet.lerp(0, MAX_ALPHA, 1 - fadePct);
		parent.fill(textColor.getRGB(), alpha);
		parent.text(fadingMessage.message, 0, 0);
		
		parent.addSettingsMessage("  --message: " + fadingMessage.message);
		parent.addSettingsMessage("  --textSize: " + textSize);
	}
	
}
