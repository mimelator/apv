package com.arranger.apv.util.draw;

import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import processing.core.PApplet;

public class TextPainter extends APVPlugin {
	
	private static final float MSG_LENGTH_CUTOFF = 20;
	public static final float TEXT_OFFSET_PCT = .9f;
	//public static final int TEXT_SIZE = 13;
	//public static final int TEXT_OFFSET = 10;

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
			
			String fontSizeString = parent.getConfigValueForFlag(Main.FLAGS.FONT_SIZE);
			int fontSize = Integer.parseInt(fontSizeString);
			int textOffset = (int)((float)fontSize * TEXT_OFFSET_PCT);
			int textSize = (int)PApplet.lerp(fontSize / 2, fontSize, pct);
			
			parent.fill(parent.getColor().getCurrentColor().getRGB());
			parent.textSize(textSize);
			
			int offset = textOffset;
			for (String s : msgs) {
				parent.text(s, textOffset, offset);
				offset += textSize;
			}
		}).paint(location);
	}
}
