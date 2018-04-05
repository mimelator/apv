package com.arranger.apv.filter;

import com.arranger.apv.Main;

import processing.core.PApplet;

public class BeatShakeFilter extends BeatFilter {
	
	private static final float SHAKE_SIZE_SMALL = -1.5f;
	private static final float SHAKE_SIZE_LARGE = 1.5f;
	
	private static final float SIZE_SCALAR_SMALL = .01f;
	private static final float SIZE_SCALAR_LARGE = 1.0f;
	
	private static final float OSC_SCALAR = 5f;
	
	public BeatShakeFilter(Main parent) {
		super(parent);
	}
	
	@Override
	public void preRender() {
		super.preRender();
		float shakeSize = parent.oscillate(SHAKE_SIZE_SMALL, SHAKE_SIZE_LARGE, OSC_SCALAR);
		float scalar = parent.oscillate(SIZE_SCALAR_SMALL, SIZE_SCALAR_LARGE, OSC_SCALAR /7);
		shakeSize *= scalar;
		
		if (beatDetect.isKick()) {
			int x = parent.width / 2;
			int y = parent.height / 2;
			parent.translate(x, y);
			parent.rotate(PApplet.radians(shakeSize));
			parent.translate(-x, -y);
		}
		parent.addDebugMsg("  --shakeSize: " + shakeSize);
	}
}
