package com.arranger.apv.filter;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

import processing.core.PApplet;

public class RotateFilter extends Filter {

	private static final float DEFAULT_ROTATE_LOW = .5f;
	private static final float DEFAULT_ROTATE_HIGH = 2.0f;
	private static final int DEFAULT_CYCLE_TIME = 20;
	
	private float rotateLow;
	private float rotateHigh;
	private int cycleTime;

	public RotateFilter(Main parent, float rotateLow, float rotateHigh, int cycleTime) {
		super(parent);
		this.rotateLow = rotateLow;
		this.rotateHigh = rotateHigh;
		this.cycleTime = cycleTime;
	}
	
	public RotateFilter(Configurator.Context ctx) {
		this(ctx.getParent(), 
				ctx.getFloat(0, DEFAULT_ROTATE_LOW),
				ctx.getFloat(1, DEFAULT_ROTATE_HIGH),
				ctx.getInt(2, DEFAULT_CYCLE_TIME));
	}
	
	
	@Override
	public String getConfig() {
		//{RotateFilter : [-720, 720, 20]}
		return String.format("{%s : [%s, %s, %s]}", getName(), rotateLow, rotateHigh, cycleTime);
	}

	@Override
	public void preRender() {
		super.preRender();
		
		float rotation = parent.oscillate(rotateLow, rotateHigh, cycleTime);
		parent.addSettingsMessage("  --rotation: " + rotation);
		
		int x = parent.width / 2;
		int y = parent.height / 2;
		parent.translate(x, y);
		parent.rotate(PApplet.radians(rotation));
		parent.translate(-x, -y);
	}
}
