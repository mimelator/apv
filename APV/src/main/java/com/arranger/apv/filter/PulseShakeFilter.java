package com.arranger.apv.filter;

import com.arranger.apv.Main;

import ddf.minim.analysis.BeatDetect;
import processing.core.PApplet;

public class PulseShakeFilter extends EarthquakeFilter {
	
	private static final float SHAKE_SIZE = 1.5f;
	private static final float SIZE_SCALAR_SMALL = .8f;
	private static final float SIZE_SCALAR_LARGE = 3.0f;
	private static final float OSC_SCALAR = 5f;

	public PulseShakeFilter(Main parent) {
		super(parent);
	}
	
	@Override
	public void preRender() {
		super.preRender();
		float shakeSize = parent.oscillate(-SHAKE_SIZE, SHAKE_SIZE, OSC_SCALAR);
		float scalar = parent.oscillate(SIZE_SCALAR_SMALL, SIZE_SCALAR_LARGE, OSC_SCALAR * 2);
		shakeSize *= scalar;
		
		BeatDetect pulseDetector = parent.getAudio().getBeatInfo().getPulseDetector();
		if (pulseDetector.isOnset()) {
			shake(shakeSize);
		}
		
		parent.addSettingsMessage("  --scalar: " + scalar);
		parent.addSettingsMessage("  --shakeSize: " + shakeSize);
	}
	
	protected void shake(float shakeSize) {
		int x = parent.width / 2;
		int y = parent.height / 2;
		parent.translate(x, y);
		if (parent.randomBoolean()) {
			parent.rotate(PApplet.radians(shakeSize));
		} else {
			parent.rotate(PApplet.radians(-shakeSize));
		}
		
		parent.translate(-x, -y);
	}
}
