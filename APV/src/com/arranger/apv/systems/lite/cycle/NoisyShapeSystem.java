package com.arranger.apv.systems.lite.cycle;

import java.awt.Color;

import com.arranger.apv.Main;

import processing.core.PVector;

/**
 * https://www.openprocessing.org/sketch/531191
 */
public class NoisyShapeSystem extends LiteCycleShapeSystem {

	private static final int HIGH_SPEED = 15; //25
	private static final int LOW_SPEED = 3; //5
	
	
	private static final int FRAMES_PER_RESET = 200;
	
	private static final int HIGH_ALPHA = 255;
	private static final int LOW_ALPHA = 20;
	
	protected float noisescale;
	protected float a1, a2, a3, a4, a5, amax;
	

	public NoisyShapeSystem(Main parent, int numNewObjects) {
		super(parent, numNewObjects);
	}

	@Override
	protected LiteCycleObj createObj(int index) {
		return new Mobile();
	}
	
	@Override
	public void setup() {
		reset();
	}
	
	protected void reset() {
		lcObjects.clear();
		
		noisescale = random(.01f, .1f);
		parent.noiseDetail(5, random(.1f, 1));
		amax = random(0, 20);
		a1 = random(1, amax);
		a2 = random(1, amax);
		a3 = random(1, amax);
		a4 = random(1, amax);
		a5 = random(LOW_SPEED, HIGH_SPEED);
		
		for (int i = 0; i < numNewObjects; i++) {
			lcObjects.add(createObj(i));
		}
	}

	@Override
	protected void createNewObjects() {
		if ((parent.frameCount % FRAMES_PER_RESET) == 0) {
			reset();
		}
	}

	private class Mobile extends LiteCycleObj {
		
		PVector velocity;
		PVector prevPos;
		PVector curPos;
		int alpha;
		int width, height;
		Color color;
		
		private Mobile() {
			width = parent.width;
			height = parent.height;
			prevPos = new PVector(random(0, width), random(0, height));
			curPos = prevPos.copy();
			alpha = (int) parent.random(LOW_ALPHA, HIGH_ALPHA);
			color = parent.getColorSystem().getCurrentColor();
		}

		public void update() {
			velocity = new PVector(1
					- 2 * parent.noise(a2 + sin(PI * curPos.x / width), sin(a2 + PI * curPos.y / height)),
					1 - 2 * parent.noise(a3 + cos(PI * curPos.x / width),
							a3 + cos(PI * curPos.y / height)));
			velocity.mult(a5);
			prevPos = curPos.copy();
			curPos.add(velocity);
		}

		public void display() {
			parent.stroke(
					parent.noise(sin(PI * curPos.x / width), sin(PI * curPos.y / height)) * 360,
					color.getGreen(), //100, 
					color.getBlue(),  //50, 
					alpha);
			parent.line(prevPos.x, prevPos.y, curPos.x, curPos.y);
			if (curPos.x > width || curPos.x < 0 || curPos.y > height || curPos.y < 0) {
				prevPos = new PVector(random(0, width), random(0, height));
				curPos = prevPos.copy();
			}
		}

		@Override
		public boolean isDead() {
			return false;
		}
	}
}
