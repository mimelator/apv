package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import processing.core.PApplet;

public class SplineHelper extends APVPlugin {

	private SplineInterpolator si = new SplineInterpolator(1, 0, 0, 1);
	
	public SplineHelper(Main parent) {
		super(parent);
	}

	public float map(float value, float start, float end, float start1, float end1) {
		value = PApplet.constrain(value, start, end);
		float pct = 1 - ((end - value) / end);
		float interpolate = (float)si.interpolate(pct);
		return PApplet.lerp(start1, end1, interpolate);
	}
}
