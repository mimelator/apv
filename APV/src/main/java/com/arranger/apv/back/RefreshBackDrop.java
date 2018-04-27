package com.arranger.apv.back;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

import processing.core.PApplet;

public class RefreshBackDrop extends BackDropSystem {

	private static final float PCT_BG_REFRESH = .8f;
	protected static final int DEFAULT_RATE = 1200;
	protected static final int RANGE = 100;
	protected float bgRefreshPct;
	protected int rate;
	
	public RefreshBackDrop(Main parent) {
		this(parent, PCT_BG_REFRESH, DEFAULT_RATE);
	}
	
	public RefreshBackDrop(Main parent, float bgRefreshPct, int rate) {
		super(parent);
		this.bgRefreshPct = bgRefreshPct;
		this.rate = rate;
	}
	
	public RefreshBackDrop(Configurator.Context ctx) {
		this(ctx.getParent(), 
				ctx.getFloat(0, PCT_BG_REFRESH),
				ctx.getInt(1, DEFAULT_RATE));
	}
	
	
	@Override
	public String getConfig() {
		//{RefreshBackDrop : [.95]}
		return String.format("{%s : [%s]}", getName(), bgRefreshPct);
	}
	
	/**
	 * Don't draw background bgRefreshPct of the time.
	 */
	@Override
	public void drawBackground() {
		float res = PApplet.map(parent.getFrameCount() % rate, 0, rate, 0, RANGE);
		if (res < bgRefreshPct * RANGE) {
			parent.background(Color.BLACK.getRGB());
		}
		parent.addSettingsMessage("  --bgRefreshPct: " + bgRefreshPct);
	}

}
