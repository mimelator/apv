package com.arranger.apv.util;

import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.ShapeSystem;

public class TextDrawHelper extends ShapeSystem {

	private List<String> msgs;
	private SafePainter.LOCATION location;
	
	public TextDrawHelper(Main parent, int numFrames, List<String> msgs, SafePainter.LOCATION location) {
		super(parent, null);
		this.msgs = msgs;
		this.location = location;
		
		new DrawHelper(parent, numFrames, this, () -> {});
	}

	@Override
	public void draw() {
		new TextPainter(parent).drawText(msgs, location);
	}

}
