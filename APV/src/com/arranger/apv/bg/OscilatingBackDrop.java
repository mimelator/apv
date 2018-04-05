package com.arranger.apv.bg;

import java.awt.Color;

import com.arranger.apv.Main;

public class OscilatingBackDrop extends BackDropSystem {
	
	private static final int OSCILLATION_SCALAR = 12; //TODO: Experiment with scalar
	private Color c1, c2;

	public OscilatingBackDrop(Main parent, Color c1, Color c2) {
		super(parent);
		this.c1 = c1;
		this.c2 = c2;
	}

	@Override
	public void drawBackground() {
		float amt = parent.oscillate(0, 1, OSCILLATION_SCALAR);
		int lerpColor = parent.lerpColor(c1.getRGB(), c2.getRGB(), amt);
		parent.background(lerpColor);
		parent.addDebugMsg(" --color from to: " + c1.getRGB() + " -> " + c2.getRGB() + " at: " + amt + "%");
	}
}
