package com.arranger.apv.systems;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;

import processing.core.PApplet;
import processing.core.PVector;

public class RotSystem extends LifecycleSystem {

	public RotSystem(Main parent, ShapeFactory factory, int numParticles) {
		super(parent, factory, numParticles);
	}

	@Override
	protected LifecycleData createData() {
		return new RotData();
	}
	
	protected class RotData extends LifecycleData {
		
		private PVector velocity;
		
		public RotData() {
			super();
		}

		public void update() {
			super.update();
			
			velocity.rotate(PApplet.radians(15));
			shape.translate(velocity.x, velocity.y);
		}
		
		protected void rebirth() {
			super.rebirth();
			Point2D p = parent.getLocationSystem().getCurrentPoint();
			float a = parent.random(PApplet.TWO_PI);
			velocity = new PVector(PApplet.cos(a), PApplet.sin(a));
			velocity.mult(parent.random(0.5f, 4));
			
			//TODO Review exact same code as GravitySystem$GravityData#rebirth
			if (shape != null && shape.getShape() != null) {
				shape.resetMatrix();
				shape.translate((float)p.getX(), (float)p.getY());
			}
		}
	}

}
