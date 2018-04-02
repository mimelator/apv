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
		
		private PVector gravity = new PVector(0, DEFAULT_GRAVITY);
		private PVector velocity;
		
		
		public void update() {
			super.update();
			
			gravity.y = parent.getGravity().getCurrentGravity();
			velocity.add(gravity);

			//move it
			shape.translate(velocity.x, velocity.y);
		}

		/**
		 * this is too clumsyfor the sub classes
		 */
		protected void rebirth() {
			super.rebirth();
			Point2D p = parent.getLocationSystem().getCurrentPoint();
			float a = parent.random(PApplet.TWO_PI);
			float speed = parent.random(0.5f, 4);
			velocity = new PVector(PApplet.cos(a), PApplet.sin(a));
			velocity.mult(speed);
			
			if (shape != null && shape.getShape() != null) {
				shape.resetMatrix();
				shape.translate((float)p.getX(), (float)p.getY());
			}
		}
	}
}
