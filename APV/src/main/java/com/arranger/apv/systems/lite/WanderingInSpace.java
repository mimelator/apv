package com.arranger.apv.systems.lite;

import java.awt.Color;

import com.arranger.apv.Main;

import processing.core.PApplet;

/**
 * @see https://www.openprocessing.org/sketch/492680
 */
public class WanderingInSpace extends LiteShapeSystem {

	Particle[] p = new Particle[800];
	int diagonal;
	float rotation = 0;

	public WanderingInSpace(Main parent) {
		super(parent);
	}

	@Override
	public void setup() {
		for (int i = 0; i < p.length; i++) {
			p[i] = new Particle();
			p[i].o = random(1, random(1, parent.width / p[i].n));
		}

		diagonal = (int) PApplet.sqrt(parent.width * parent.width + parent.height * parent.height) / 2;
	}

	@Override
	public void draw() {
		parent.translate(parent.width / 2, parent.height / 2);
		rotation -= 0.002;
		parent.rotate(rotation);

		for (int i = 0; i < p.length; i++) {
			p[i].draw();
			if (p[i].drawDist() > diagonal) {
				p[i] = new Particle();
			}
		}
	}

	class Particle {
		float n;
		float r;
		float o;
		int l;

		Particle() {
			l = 1;
			n = random(1, parent.width / 2);
			r = random(0, TWO_PI);
			o = random(1, random(1, parent.width / n));
		}

		void draw() {
			Color c = parent.getColor().getCurrentColor();
			l++;
			parent.pushMatrix();
			parent.rotate(r);
			parent.translate(drawDist(), 0);
			parent.fill(c.getRGB(), PApplet.min(l, 255));
			parent.ellipse(0, 0, parent.width / o / 8, parent.width / o / 8);
			parent.popMatrix();

			o -= 0.07;
		}

		float drawDist() {
			return PApplet.atan(n / o) * parent.width / HALF_PI;
		}
	}
}