package com.arranger.apv.systems;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * For each APVShape manage:
 *	
 *  Gravity
 *  Translation
*/
public class GravitySystem extends LifecycleSystem {

	public GravitySystem(Main parent, ShapeFactory factory, int numParticles) {
		super(parent, factory, numParticles);
	}
	
	@Override
	protected LifecycleData createData() {
		return new GravityData();
	}


	protected class GravityData extends LifecycleData {
		private static final float DEFAULT_GRAVITY = 0.1f;
		
		protected PVector gravity = new PVector(0, DEFAULT_GRAVITY);
		protected PVector velocity;
		
		
		public void update() {
			super.update();
			
			updateGravity();
			updateLocation();
		}
		
		protected void centerShape() {
			int y = parent.height / 2;
			int x = parent.width / 2;
			shape.translate(x, y); //Center the shape
		}

		protected void updateLocation() {
			shape.translate(velocity.x, velocity.y);
		}

		/**
		 * called from {@link #update()} and changes the gravity based on the global gravity 
		 */
		protected void updateGravity() {
			gravity.y = parent.getGravity().getCurrentGravity();
			velocity.add(gravity);
		}

		/**
		 * TODO this is too clumsy for the sub classes
		 */
		protected void respawn() {
			super.respawn();
			
			//pick a direction
			float a = parent.random(PApplet.TWO_PI);
			velocity = new PVector(PApplet.cos(a), PApplet.sin(a));

			//get the speed
			float speed = parent.random(0.5f, 4);
			velocity.mult(speed);

			//Set initial location
			setInitialLocation();
		}

		/**
		 * Called from respawn
		 */
		protected void setInitialLocation() {
			Point2D p = parent.getLocationSystem().getCurrentPoint();
			if (shape != null && shape.getShape() != null) {
				shape.resetMatrix();
				shape.translate((float)p.getX(), (float)p.getY());
			}
		}
	}
}
