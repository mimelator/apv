package com.arranger.apv.systems.lite;

import java.awt.Color;

import com.arranger.apv.Main;

import processing.core.PApplet;

/**
 * @see https://www.openprocessing.org/sketch/482436
 */
public class FloatingGeometry extends LiteShapeSystem {

	public FloatingGeometry(Main parent) {
		super(parent);
	}

	@Override
	public void setup() {

	}

	@Override
	public void draw() {
		Color currentColor = parent.getColor().getCurrentColor();
		
		float yOffset = radius * 2;
		for (float y = space; y <= parent.height - space; y += yOffset) {
			for (float x = space; x < parent.width - space; x += radius * 2) {
				float xOffset = 0;
				if ((y % (yOffset * 2) == 0)) {
					xOffset = radius;
				} else {
					xOffset = 0;
				}
				shape(x + xOffset, y, radius * .85f, currentColor);
			}
		}
	}

	float radius = 20;
	float space = 40;

	void shape(float x, float y, float r, Color currentColor) {
		
		int width = parent.width;
		int height = parent.height;
		float a = PApplet.dist(x, y, width / 2, height / 2) - parent.getFrameCount() * .8f;
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
