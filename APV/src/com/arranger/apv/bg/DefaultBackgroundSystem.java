package com.arranger.apv.bg;

import java.awt.Color;

import com.arranger.apv.Main;

import processing.core.PApplet;

public class DefaultBackgroundSystem extends BackDropSystem {

	protected static final int RATE = 1200;
	protected static final int RANGE = 100;
	
	public DefaultBackgroundSystem(Main parent) {
		super(parent);
	}
	
	/**
	 * //Don't draw background 20% of the time.
	 */
	@Override
	public void drawBackground() {
		float res = PApplet.map(parent.frameCount % RATE, 0, RATE, 0, RANGE);
		if (res < .8f * RANGE) {
			parent.background(Color.BLACK.getRGB());
		}
	}

}
