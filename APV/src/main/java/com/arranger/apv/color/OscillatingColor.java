package com.arranger.apv.color;

import java.awt.Color;

import com.arranger.apv.Main;

public class OscillatingColor extends BeatColorSystem {

	private static final int DEFAULT_SCALAR = 5;
	private float oscScalar = DEFAULT_SCALAR;
	
	public OscillatingColor(Main parent) {
		super(parent);
	}
	
	public OscillatingColor(Main parent, float oscScalar) {
		super(parent);
		this.oscScalar = oscScalar;
	}
	
	public Color getCurrentColor() {
		float hue = parent.oscillate(0, 1, oscScalar);
		float saturation = 1;
		float brightness = 1;
		
		return Color.getHSBColor(hue, saturation, brightness);
	}
}