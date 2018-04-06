package com.arranger.apv.filter;

import java.awt.Color;

import com.arranger.apv.Main;

import processing.core.PApplet;

public class BlendModeFilter extends Filter {
	
	public enum BLEND_MODE {
			BLEND(PApplet.BLEND),
			ADD(PApplet.ADD),
			SUBTRACT(PApplet.SUBTRACT),
			DARKEST(PApplet.DARKEST),
			LIGHTEST(PApplet.LIGHTEST),
			DIFFERENCE(PApplet.DIFFERENCE),
			EXCLUSION(PApplet.EXCLUSION),
			MULTIPLY(PApplet.MULTIPLY),
			SCREEN(PApplet.SCREEN),
			REPLACE(PApplet.REPLACE);
	
			private int mode;
			
			BLEND_MODE(int mode) {
				this.mode = mode;
			}
			public int mode() {return mode;}
	};
	
	BLEND_MODE blendMode;
	Color color = Color.BLACK;

	public BlendModeFilter(Main parent, BLEND_MODE blendMode) {
		super(parent);
		this.blendMode = blendMode;
	}

	@Override
	public void preRender() {
		super.preRender();
		parent.blendMode(blendMode.mode());
		parent.addDebugMsg("  --blendMode: " + blendMode);
	}
}
