package com.arranger.apv.util;

import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import processing.core.PApplet;

public class TextPainter extends APVPlugin {
	
	private static final float MSG_LENGTH_CUTOFF = 20;
	public static final int TEXT_SIZE = 16;
	public static final int TEXT_OFFSET = 10;

	public TextPainter(Main parent) {
		super(parent);
	}
	
	public void drawText(List<String> msgs) {
		drawText(msgs, null);
	}
	
	public void drawText(List<String> msgs, SafePainter.LOCATION location) {
		new SafePainter(parent, () -> {
			
			//Configure text size based on num msgs
			float pct = 1.0f;
			float msgCount = msgs.size();
			if (msgCount > MSG_LENGTH_CUTOFF) {
				pct = 1.0f - (((msgCount / MSG_LENGTH_CUTOFF) - 1.0f) / 10.0f);
			}
			int textSize = (int)PApplet.lerp(TEXT_SIZE / 2, TEXT_SIZE, pct);
			
			parent.fill(255);
			parent.textAlign(PApplet.LEFT, PApplet.TOP);
			parent.textSize(textSize);
			
			int offset = TEXT_OFFSET;
			for (String s : msgs) {
				parent.text(s, TEXT_OFFSET, offset);
				offset += textSize;
			}
		}).paint(location);
	}
}
