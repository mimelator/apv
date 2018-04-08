package com.arranger.apv.systems.lifecycle;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;

import processing.core.PApplet;
import processing.core.PVector;

public class RotSystem extends LifecycleSystem {

	public static final int ROTATION_SPEED = 1;
	
	public RotSystem(Main parent, ShapeFactory factory, int numParticles) {
		super(parent, factory, numParticles);
	}
	
	@Override
	protected LifecycleData createData() {
		return new RotData();
	}
	
	protected class RotData extends LifecycleData {
		
		private static final int HIGH_SPEED_RANGE = 4;
		private static final float LOW_SPEED_RANGE = 0.5f;
		
		protected float heading; 
		protected PVector dist; //distance from center of the screen
		protected PVector velocity;
		protected Point2D p; //initial location
		
		public RotData() {
			super();
		}

		/**
		 * {@link RotSystem#draw()}
		 * 
		 * Resets the matrix of the shape each time
		 */
		public void update() {
			super.update();
			shape.resetMatrix();
			
			dist.add(velocity);
			heading += ROTATION_SPEED;
			
			shape.rotate(PApplet.radians(heading));
			shape.translate((float)p.getX(), (float)p.getY()); //Starting point
			shape.translate(dist.x, dist.y); //move it along
		}

		/**
		 * called when {@link #isDead()} is true during the update/draw phase
		 * Also called during {@link GravitySystem#setup()}
		 */
		protected void respawn() {
			super.respawn();
			
			//pick a heading
			heading = PApplet.degrees(parent.random(PApplet.TWO_PI));
			dist = new PVector();
			
			//get the speed and orientation
			float a = parent.random(PApplet.TWO_PI);
			velocity = new PVector(PApplet.cos(a), PApplet.sin(a));
			
			float speed = parent.random(LOW_SPEED_RANGE, HIGH_SPEED_RANGE);
			velocity.mult(speed);

			//Set initial location
			setInitialLocation();
		}

		/**
		 * called during {@link #respawn()}
		 */
		protected void setInitialLocation() {
			p = parent.getLocationSystem().getCurrentPoint();
			if (shape != null && shape.getShape() != null) {
				shape.resetMatrix();
				shape.translate((float)p.getX(), (float)p.getY());
			}
		}
	}
}
