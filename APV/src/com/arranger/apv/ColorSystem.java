package com.arranger.apv;

import java.awt.Color;

import ddf.minim.analysis.BeatDetect;

/**
 * Basic BeatColor System toggles between red & white
 */
public class ColorSystem {

	protected Main parent;
	
	public ColorSystem(Main parent) {
		this.parent = parent;
	}

	public Color getCurrentColor() {
		BeatDetect beat = parent.getAudio().getBeatInfo().getBeat();
		boolean kick = beat.isKick();
		return kick ? Color.RED : Color.WHITE;
	}
	
	public static class RandomColor extends ColorSystem {

		public RandomColor(Main parent) {
			super(parent);
		}
		
		public Color getCurrentColor() {
			float hue = parent.random(1.0f);
			float saturation = 1;
			float brightness = 1;
			
			return Color.getHSBColor(hue, saturation, brightness);
		}
	}
	
	public static class OscillatingColor extends ColorSystem {

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
}
