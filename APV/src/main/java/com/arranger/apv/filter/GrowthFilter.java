package com.arranger.apv.filter;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class GrowthFilter extends Filter {

	private static final float DEFAULT_SCALE_LOW = .5f;
	private static final float DEFAULT_SCALE_HIGH = 2.0f;
	private static final int DEFAULT_CYCLE_TIME = 20;
	
	private float scaleLow;
	private float scaleHigh;
	private int cycleTime;

	public GrowthFilter(Main parent, float scaleLow, float scaleHigh, int cycleTime) {
		super(parent);
		this.scaleLow = scaleLow;
		this.scaleHigh = scaleHigh;
		this.cycleTime = cycleTime;
	}
	
	public GrowthFilter(Configurator.Context ctx) {
		this(ctx.getParent(), 
				ctx.getFloat(0, DEFAULT_SCALE_LOW),
				ctx.getFloat(1, DEFAULT_SCALE_HIGH),
				ctx.getInt(2, DEFAULT_CYCLE_TIME));
	}
	
	
	@Override
	public String getConfig() {
		//{GrowthFilter : [1, 2, 20]}
		return String.format("{%s : [%s, %s, %s]}", getName(), scaleLow, scaleHigh, cycleTime);
	}

	@Override
	public void preRender() {
		super.preRender();
		
		float scaleFactor = parent.oscillate(scaleLow, scaleHigh, cycleTime);
		parent.addSettingsMessage("  --scaleFactor: " + scaleFactor);
		
		parent.scale(scaleFactor);
	}

}
