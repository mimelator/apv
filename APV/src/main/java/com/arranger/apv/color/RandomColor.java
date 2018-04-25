package com.arranger.apv.color;

import java.awt.Color;

import com.arranger.apv.Main;

public class RandomColor extends BeatColorSystem {

	public RandomColor(Main parent) {
		super(parent);
	}
	
	@Override
	protected Color createColor() {
		float hue = parent.random(1.0f);
		float saturation = 1;
		float brightness = 1;
		
		return Color.getHSBColor(hue, saturation, brightness);
	}
}