package com.arranger.apv.systems;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;

import processing.core.PApplet;
import processing.core.PVector;

public class RotSystem extends GravitySystem {

	public static final int DEGREES = 360;
	public static final int ROTATION_SPEED = 1;
	
	public RotSystem(Main parent, ShapeFactory factory, int numParticles) {
		super(parent, factory, numParticles);
	}

	@Override
	protected LifecycleData createData() {
		return new RotData();
	}
	
	protected class RotData extends GravityData {
		
		
		protected float heading; 
		protected PVector dist; //distance from center of the screen
		protected Point2D p; //initial location
		
		public RotData() {
			super();
		}

		public void update() {
			super.update();
			shape.resetMatrix();
			
			dist.add(velocity);
			heading += ROTATION_SPEED;
			
			shape.rotate(PApplet.radians(heading));
			shape.translate((float)p.getX(), (float)p.getY()); //Starting point
			shape.translate(dist.x, dist.y); //move it along
		}
		
		protected void updateLocation() {
			//Do nothing here
		}
		
		@Override
		protected void updateGravity() {
			//Don't use gravity
		}

		protected void respawn() {
			super.respawn();
			heading = parent.random(DEGREES);
			dist = new PVector();
		}

		@Override
		protected void setInitialLocation() {
			p = parent.getLocationSystem().getCurrentPoint();
		}
	}
}
