package com.arranger.apv.bg;

import java.awt.Color;

import com.arranger.apv.Main;

import processing.core.PApplet;

public class DefaultBackgroundSystem extends BackDropSystem {

	private static final float PCT_BG_REFRESH = .8f;
	protected static final int RATE = 1200;
	protected static final int RANGE = 100;
	protected float bgRefreshPct;
	
	public DefaultBackgroundSystem(Main parent) {
		this(parent, PCT_BG_REFRESH);
	}
	
	public DefaultBackgroundSystem(Main parent, float bgRefreshPct) {
		super(parent);
		this.bgRefreshPct = bgRefreshPct;
	}
	
	/**
	 * //Don't draw background 20% of the time.
	 */
	@Override
	public void drawBackground() {
		float res = PApplet.map(parent.frameCount % RATE, 0, RATE, 0, RANGE);
		if (res < bgRefreshPct * RANGE) {
			parent.background(Color.BLACK.getRGB());
		}
		parent.addDebugMsg("bgRefreshPct: " + bgRefreshPct);
	}

}
