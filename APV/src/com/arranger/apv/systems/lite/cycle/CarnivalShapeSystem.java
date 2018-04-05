package com.arranger.apv.systems.lite.cycle;


import java.awt.geom.Point2D;

import com.arranger.apv.BeatColorSystem;
import com.arranger.apv.BeatColorSystem.OscillatingColor;
import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * https://www.openprocessing.org/sketch/521068
 */
public class CarnivalShapeSystem extends LiteCycleShapeSystem {
	
	private boolean useCustomColor = false;
	private BeatColorSystem colorSystem;

	public CarnivalShapeSystem(Main parent, ShapeFactory factory) {
		super(parent);
		this.factory = factory; 
	}
	
	public CarnivalShapeSystem(Main parent, ShapeFactory factory, boolean useCustomColor) {
		super(parent);
		this.factory = factory;
		this.useCustomColor = useCustomColor;
	}

	@Override
	public void setup() {
		shouldCreateNewObjectsEveryDraw = true;
		if (useCustomColor) {
			colorSystem = new OscillatingColor(parent);
		}
	}

	public void draw() {
		parent.colorMode(HSB);
		parent.rectMode(CENTER);
		
		super.draw();
	}
	
	@Override
	protected void reset() {
		
	}

	@Override
	protected LiteCycleObj createObj(int index) {
		Point2D point = parent.getLocationSystem().getCurrentPoint();
		float x = (float)point.getX();
		float y = (float)point.getY();
		int curSize = lcObjects.size();
		return new Particle(x, y, index + curSize, index + curSize);
	}

	private class Particle extends LiteCycleObj {
		private PVector loc, vel, acc;
		private int lifeSpan, passedLife;
		private boolean dead;
		private float alpha, weight, weightRange, decay, xOfst, yOfst;
		private int c;

		private Particle(float x, float y, float xOfst, float yOfst) {
			loc = new PVector(x, y);

			float randDegrees = parent.random(360);
			vel = new PVector(PApplet.cos(PApplet.radians(randDegrees)), PApplet.sin(PApplet.radians(randDegrees)));
			vel.mult(parent.random(5));

			acc = new PVector(0, 0);
			lifeSpan = (int) (parent.random(30, 90));
			decay = parent.random(0.75f, 0.9f);
			
			BeatColorSystem cs = (useCustomColor) ? colorSystem : parent.getColorSystem();
			c = cs.getCurrentColor().getRGB();

			//Original color scheme
			//c = parent.color(parent.random(255), parent.random(255), 255);
			weightRange = parent.random(3, 50);

			this.xOfst = xOfst;
			this.yOfst = yOfst;
		}

		@Override
		public boolean isDead() {
			return dead;
		}
		
		@Override
		public void update() {
			if (passedLife >= lifeSpan) {
				dead = true;
			} else {
				passedLife++;
			}

			int frameCount = parent.frameCount;
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

		@Override
		public void display() {
			//TODO Use Shape Factory
			
			parent.strokeWeight(weight + 1.5f);
			parent.stroke(0, alpha);
			parent.point(loc.x, loc.y);

			parent.strokeWeight(weight);
			parent.stroke(c);
			parent.point(loc.x, loc.y);
		}
	}
}
