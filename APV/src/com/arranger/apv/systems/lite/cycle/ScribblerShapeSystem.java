package com.arranger.apv.systems.lite.cycle;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;

import processing.core.PApplet;

/**
 * https://www.openprocessing.org/sketch/444760
 * 
 * This is one of the only background systems that responds to location
 */
public class ScribblerShapeSystem extends LiteCycleShapeSystem {
	
	private static final int SCRIBBLER_DEFAULT_FRAMES_PER_RESET = 1500;
	private static final float MAX_VELOCITY = 2.5f;
	private static final float MAX_STROKE_WEIGHT = 1.5f;
	
	public ScribblerShapeSystem(Main parent) {
		super(parent);
	}

	public ScribblerShapeSystem(Main parent, int numNewObjects) {
		super(parent, numNewObjects);
	}

	@Override
	public void setup() {
		shouldCreateNewObjectsEveryDraw = false;
		shouldRepopulateObjectsEveryDraw = true;
		framesPerReset = SCRIBBLER_DEFAULT_FRAMES_PER_RESET;
		super.setup();
	}
	
	@Override
	protected LiteCycleObj createObj(int index) {
		return new Scribbler();
	}
	
	private class Scribbler extends LiteCycleObj {
		float prevX = 0, prevY = 0;
		float theta;
		float distance;
		float thetaV;
		float distanceV;
		boolean mvTheta = false;
		float anchorX, anchorY;
		float strokeWeight;
		Color strokeColor;
		
		@Override
		public boolean isDead() {
			return distance > parent.height / 2;
		}

		@Override
		public void display() {
			//all work is done in update
		}

		public Scribbler() {
			Point2D currentPoint = parent.getLocationSystem().getCurrentPoint();
			prevX = (float)currentPoint.getX();
			prevY = (float)currentPoint.getY();
			anchorX = prevX;
			anchorY = prevY;
			
			randomizeVelocities();
			
			theta = random(TWO_PI);
			strokeWeight = PApplet.round(1 + random(MAX_STROKE_WEIGHT));
			strokeColor = parent.getColorSystem().getCurrentColor();
		}

		@Override
		public void update() {
			float curX = anchorX + (cos(theta) * distance);
			float curY = anchorY + (sin(theta) * distance);

			parent.pushStyle();
			parent.stroke(strokeColor.getRGB());
			parent.strokeWeight(distance * strokeWeight * 0.01f);
			parent.line(prevX, prevY, curX, curY); //TODO Explore using ShapeFactory
			parent.popStyle();

			prevX = curX;
			prevY = curY;

			if (mvTheta) {
				theta += thetaV;
			} else {
				distance += distanceV;
			}

			if (random(1) > 0.9) {
				swapMode();
			}
		}

		void swapMode() {
			mvTheta = !mvTheta;
			randomizeVelocities();
		}

		void randomizeVelocities() {
			thetaV = -0.05f + random(0.1f);
			distanceV = 0.2f + random(MAX_VELOCITY);
		}

	}
}
