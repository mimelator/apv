package com.arranger.apv.back;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

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
	
	public RefreshBackDrop(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getFloat(0, PCT_BG_REFRESH));
	}
	
	
	@Override
	public String getConfig() {
		//{RefreshBackDrop : [.95]}
		String name = getName();
		return String.format("{%1s : [%2s]}", name, bgRefreshPct);
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
