package com.arranger.apv.systems.lite;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.util.FFTAnalysis;

import processing.core.PApplet;

/**
 * @see https://www.openprocessing.org/sketch/482436
 */
public class FloatingGeometry extends LiteShapeSystem {

	private static final float MAX_AMP_SCALAR = .2f;
	private static final float ALPHA_SCALAR = .04f;
	private static final float RADIUS = 20;
	private static final float SPACE = 40;
	
	private FFTAnalysis fftAnalysis;
	
	public FloatingGeometry(Main parent) {
		super(parent);
		this.fftAnalysis = new FFTAnalysis(parent);
	}

	@Override
	public void setup() {

	}

	@Override
	public void draw() {
		Color currentColor = parent.getColor().getCurrentColor();
		
		float yOffset = RADIUS * 2;
		for (float y = SPACE; y <= parent.height - SPACE; y += yOffset) {
			for (float x = SPACE; x < parent.width - SPACE; x += RADIUS * 2) {
				float xOffset = 0;
				if ((y % (yOffset * 2) == 0)) {
					xOffset = RADIUS;
				} else {
					xOffset = 0;
				}
				shape(x + xOffset, y, RADIUS * .85f, currentColor);
			}
		}
	}

	void shape(float x, float y, float r, Color currentColor) {
		
		int width = parent.width;
		int height = parent.height;
		
		float ampScalar = fftAnalysis.getMappedAmp(0, 3, 1, MAX_AMP_SCALAR);
		float alphaScalar = parent.getFrameCount() * ALPHA_SCALAR * ampScalar;
		float a = PApplet.dist(x, y, width / 2, height / 2) - alphaScalar;
		float r2 = PApplet.map(sin(PApplet.radians(a)), 0, 1, 0, r);
		float col = PApplet.map(PApplet.dist(x, y, width / 2, height / 2), 0, 
								PApplet.dist(0, 0, width / 2, height / 2), 200, -30);
		parent.fill(currentColor.getRGB(), col);
		parent.beginShape();
		for (int i = 0; i < 3; i++) {
			float angle = -PI * i / 2;
			parent.vertex(x + cos(angle) * r2, y + sin(angle) * r2);
		}
		parent.endShape();
	}

}
