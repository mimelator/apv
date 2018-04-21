package com.arranger.apv.archive;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.systems.lite.LiteShapeSystem;

import processing.core.PApplet;

/**
 * https://www.openprocessing.org/sketch/496790
 **/
public class LightSaber extends LiteShapeSystem {

	private static final int NUM_LINES_FOR_SABER = 13;
	private static final float PREV_MOUSE_MODIFIER = 1.5f;
	int core;
	int rim;
	float a = 0;
	float a2 = 0;
	float mx2, my2;

	public LightSaber(Main parent) {
		super(parent);
	}

	@Override
	public void setup() {
		core = parent.color(255);
	}

	@Override
	public void draw() {
		rim = parent.getColor().getCurrentColor().getRGB();
		
		parent.fill(0, 128);
		parent.noStroke();
		parent.rectMode(CORNER);

		Point2D cp = parent.getCurrentPoint();
		int mouseX = (int) cp.getX();
		int mouseY = (int) cp.getY();
		float pmouseX = mouseX * PREV_MOUSE_MODIFIER;

		float dx = mouseX - pmouseX;
		a -= (dx / 2);
		a /= 8;

		a2 += (a - a2) / 16;
		float x2 = sin(a2);
		float y2 = cos(a2);

		mx2 += (mouseX - mx2) / 4;
		my2 += (mouseY - my2) / 4;

		handle(mx2, my2, mx2 + 100 * x2, my2 + 100 * y2);
		lazer(mx2, my2, mx2 - 400 * x2, my2 - 400 * y2);
	}

	void handle(float x1, float y1, float x2, float y2) {
		parent.stroke(50);
		for (float i = 1; i < 7; i++) {
			if (i == 3) {
				parent.fill(140, 0, 0);
				parent.strokeWeight(6);
			} else {
				parent.strokeWeight(4);
				parent.fill(80);
			}
			parent.ellipse(PApplet.lerp(x1, x2, i / 7), PApplet.lerp(y1, y2, i / 7), 21, 21);
		}
	}

	float t = 0;

	void lazer(float x1, float y1, float x2, float y2) {
		t += 0.1;
		for (float n = NUM_LINES_FOR_SABER; n > 1; n--) {
			float m = n / NUM_LINES_FOR_SABER;
			// m*=m;
			m = 1 - m;
			m *= m;
			parent.stroke(parent.lerpColor(rim, core, m));
			parent.strokeWeight(n);
			parent.line(
					x1 + rnd(0, n, t) * m * 20, 
					y1 + rnd(1, n, t) * n, 
					x2 + rnd(2, n, t) * n,
					y2 + rnd(3, n, t) * n);
		}
	}

	float rnd(float a, float b, float c) {
		return parent.noise(a, b, c) - 0.5f;
	}
}
