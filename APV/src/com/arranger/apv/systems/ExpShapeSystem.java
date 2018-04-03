package com.arranger.apv.systems;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;
import com.arranger.apv.ShapeSystem;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PVector;

/**
 * Adapt to look at this: https://www.openprocessing.org/sketch/521068
 * @author markimel
 *
 */
public class ExpShapeSystem extends ShapeSystem implements PConstants {

	private static final int NUM_NEW_PARTICLES = 10;
	ArrayList<Particle> pts;
	boolean onPressed, showInstruction = true;
	PFont f;

	public ExpShapeSystem(Main parent, ShapeFactory factory) {
		super(parent, factory);
	}

	@Override
	public void setup() {
		pts = new ArrayList<Particle>();
		
	}

	@Override
	public void draw() {
		parent.pushMatrix();
		parent.colorMode(HSB);
		parent.rectMode(CENTER);
		
		Point2D point = parent.getLocationSystem().getCurrentPoint();
		float x = (float)point.getX();
		float y = (float)point.getY();
		
		for (int i = 0; i < NUM_NEW_PARTICLES; i++) {
			Particle newP = new Particle(x, y, i + pts.size(), i + pts.size());
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
		parent.colorMode(RGB);
		parent.popMatrix();
	}

	class Particle {
		PVector loc, vel, acc;
		int lifeSpan, passedLife;
		boolean dead;
		float alpha, weight, weightRange, decay, xOfst, yOfst;
		int c;

		Particle(float x, float y, float xOfst, float yOfst) {
			loc = new PVector(x, y);

			float randDegrees = parent.random(360);
			vel = new PVector(PApplet.cos(PApplet.radians(randDegrees)), PApplet.sin(PApplet.radians(randDegrees)));
			vel.mult(parent.random(5));

			acc = new PVector(0, 0);
			lifeSpan = (int) (parent.random(30, 90));
			decay = parent.random(0.75f, 0.9f);
			c = parent.color(parent.random(255), parent.random(255), 255);
			weightRange = parent.random(3, 50);

			this.xOfst = xOfst;
			this.yOfst = yOfst;
		}

		void update() {
			int frameCount = parent.frameCount;
			
			if (passedLife >= lifeSpan) {
				dead = true;
			} else {
				passedLife++;
			}

			alpha = (float) (lifeSpan - passedLife) / lifeSpan * 70 + 50;
			weight = (float) (lifeSpan - passedLife) / lifeSpan * weightRange;

			acc.set(0, 0);

			float rn = (parent.noise((loc.x + frameCount + xOfst) * .01f, 
					(loc.y + frameCount + yOfst) * .01f) - .5f) * TWO_PI * 4;
			float mag = parent.noise((loc.y - frameCount) * .01f, (loc.x - frameCount) * .01f);
			PVector dir = new PVector(PApplet.cos(rn), PApplet.sin(rn));
			acc.add(dir);
			acc.mult(mag);

			float randRn = parent.random(TWO_PI);
			PVector randV = new PVector(PApplet.cos(randRn), PApplet.sin(randRn));
			randV.mult(.25f);
			acc.add(randV);

			vel.add(acc);
			vel.mult(decay);
			vel.limit(3);
			loc.add(vel);
		}

		void display() {
			parent.strokeWeight(weight + 1.5f);
			parent.stroke(0, alpha);
			parent.point(loc.x, loc.y);

			parent.strokeWeight(weight);
			parent.stroke(c);
			parent.point(loc.x, loc.y);
		}
	}
}
