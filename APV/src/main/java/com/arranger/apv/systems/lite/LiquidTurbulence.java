package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.Main;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * @see https://www.openprocessing.org/sketch/448633
 */
public class LiquidTurbulence extends LiteShapeSystem {

	private static final int LIFE_SPAN = 90;
	private static final int NEW_PARTICLES = 10;
	private static final float DECAY_RATE = .95f;//0.75f;
	private static final float SIZE_MULT = .5f;
	
	List<Particle> pts = new ArrayList<Particle>();
	
	public LiquidTurbulence(Main parent) {
		super(parent);
	}

	@Override
	public void draw() {
		parent.rectMode(CENTER);

		Point2D pt = parent.getCurrentPoint();
		int mouseX = (int) pt.getX();
		int mouseY = (int) pt.getY();
		
		for (int i = 0; i < NEW_PARTICLES; i++) {
			Particle newP = new Particle(mouseX, mouseY, i + pts.size(), i + pts.size());
			pts.add(newP);
		}

		for (int i = pts.size() - 1; i > -1; i--) {
			Particle p = pts.get(i);
			if (p.dead) {
				pts.remove(i);
			} else {
				p.update();
				p.display();
			}
		}
	}

	class Particle {
		PVector loc, vel, acc;
		int lifeSpan, passedLife;
		boolean dead;
		float xOfst, yOfst;
		Color color;

		Particle(float x, float y, float xOfst, float yOfst) {
			loc = new PVector(x, y);

			float randDegrees = random(360);
			vel = new PVector(cos(PApplet.radians(randDegrees)), sin(PApplet.radians(randDegrees)));
			vel.mult(random(5));

			acc = new PVector(0, 0);
			lifeSpan = LIFE_SPAN;

			color = parent.getColor().getCurrentColor();

			this.xOfst = xOfst;
			this.yOfst = yOfst;
		}

		void update() {
			int frameCount = parent.getFrameCount();
			
			if (passedLife >= lifeSpan) {
				dead = true;
			} else {
				passedLife++;
			}

			acc.set(0, 0);

			float rn = (parent.noise((loc.x + frameCount + xOfst) * .01f, (loc.y + frameCount + yOfst) * .01f) - .5f) * TWO_PI
					* 4;
			float mag = parent.noise((loc.y - frameCount) * .01f, (loc.x - frameCount) * .01f);
			PVector dir = new PVector(cos(rn), sin(rn));
			acc.add(dir);
			acc.mult(mag);

			float randRn = random(TWO_PI);
			PVector randV = new PVector(cos(randRn), sin(randRn));
			randV.mult(.25f);
			acc.add(randV);

			vel.add(acc);
			vel.mult(DECAY_RATE);
			vel.limit(3);
			loc.add(vel);
		}

		void display() {
			parent.strokeWeight((lifeSpan - passedLife) * SIZE_MULT);
			parent.stroke(color.getRGB(), 20);
			parent.point(loc.x, loc.y);
		}
	}
}
