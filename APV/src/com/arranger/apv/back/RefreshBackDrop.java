package com.arranger.apv.back;

import java.awt.Color;

import com.arranger.apv.Main;

import processing.core.PApplet;

public class RefreshBackDrop extends BackDropSystem {

	private static final float PCT_BG_REFRESH = .8f;
	protected static final int RATE = 1200;
	protected static final int RANGE = 100;
	protected float bgRefreshPct;
	
	public RefreshBackDrop(Main parent) {
		this(parent, PCT_BG_REFRESH);
	}
	
	public RefreshBackDrop(Main parent, float bgRefreshPct) {
		super(parent);
		this.bgRefreshPct = bgRefreshPct;
	}
	
	/**
	 * Don't draw background bgRefreshPct of the time.
	 */
	@Override
	public void drawBackground() {
		float res = PApplet.map(parent.getFrameCount() % RATE, 0, RATE, 0, RANGE);
		if (res < bgRefreshPct * RANGE) {
			parent.background(Color.BLACK.getRGB());
		}
		parent.addSettingsMessage("  --bgRefreshPct: " + bgRefreshPct);
	}

}
