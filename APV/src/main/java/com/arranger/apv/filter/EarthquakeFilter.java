package com.arranger.apv.filter;

import com.arranger.apv.Main;

import processing.core.PApplet;

public class EarthquakeFilter extends Filter {

	private static final float SHAKE_SIZE = .7f;
	private static final float SIZE_SCALAR_SMALL = .4f;
	private static final float SIZE_SCALAR_LARGE = 1.5f;
	private static final float OSC_SCALAR = 3f;
	
	public EarthquakeFilter(Main parent) {
		super(parent);
	}

	@Override
	public void preRender() {
		super.preRender();
		
		float shakeSize = parent.oscillate(-SHAKE_SIZE, SHAKE_SIZE, OSC_SCALAR);
		float scalar = parent.oscillate(SIZE_SCALAR_SMALL, SIZE_SCALAR_LARGE, OSC_SCALAR * 2);
		shakeSize *= scalar;
		shake(shakeSize);
	}
	
	protected void shake(float shakeSize) {
		int x = parent.width / 2;
		int y = parent.height;
		parent.translate(x, y);
		if (parent.randomBoolean()) {
			parent.rotate(PApplet.radians(shakeSize));
		} else {
			parent.rotate(PApplet.radians(-shakeSize));
		}
		
		parent.translate(-x, -y);
	}
}
