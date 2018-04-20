package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class SafePainter extends APVPlugin {
	
	private Painter painter;
	
	@FunctionalInterface
	public static interface Painter {
		void paint();
	}

	public SafePainter(Main parent, Painter painter) {
		super(parent);
		this.painter = painter;
	}

	public void paint() {
		parent.pushStyle();
		parent.pushMatrix();
		painter.paint();
		parent.popMatrix();
		parent.popStyle();
	}
}
