package com.arranger.apv.systems.lifecycle;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FFTAnalysis;

import processing.core.PApplet;
import processing.core.PVector;

public class RotatorSystem extends LifecycleSystem {

	private static final float MAX_ROTATION_SCALAR = 1.15f;
	private static final float RANGE_RANDOM = .05f;
	public static final int ROTATION_SPEED = 1;
	
	private FFTAnalysis fftAnalysis;
	
	public RotatorSystem(Main parent, ShapeFactory factory, int numParticles) {
		super(parent, factory, numParticles);
		fftAnalysis = new FFTAnalysis(parent);
	}
	
	public RotatorSystem(Configurator.Context ctx) {
		this(ctx.getParent(), 
				(ShapeFactory)ctx.loadPlugin(0), 
				ctx.getInt(1, Main.NUMBER_PARTICLES));
	}
	
	/**
	 * Delegate to the LocationSystem
	 */
	@Override
	public String getDisplayName() {
		return super.getDisplayName() + "[" + parent.getLocation().getDisplayName() + "]";
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
		protected float uniqueOffset;
		protected float rotationSpeed = ROTATION_SPEED;
		protected boolean isReverse = parent.randomBoolean();
		
		public RotData() {
			super();
			uniqueOffset = parent.random(1 - RANGE_RANDOM, 1 + RANGE_RANDOM);
		}

		/**
		 * {@link RotatorSystem#draw()}
		 * 
		 * Resets the matrix of the shape each time
		 */
		public void update() {
			super.update();
			shape.resetMatrix();
			
			dist.add(velocity);
			
			//augment the rotation speed with the FFT amp and RotData random
			float rotationScalar = fftAnalysis.getMappedAmp(0, 1, 1, MAX_ROTATION_SCALAR);
			if (isReverse) {
				rotationScalar = -rotationScalar;
			}
			rotationSpeed += rotationScalar;
			heading += rotationSpeed * uniqueOffset;
			
//			float width = shape.getWidth();
//			float height = shape.getHeight();
			
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
			p = parent.getLocation().getCurrentPoint();
			if (shape != null && shape.getShape() != null) {
				shape.resetMatrix();
				shape.translate((float)p.getX(), (float)p.getY());
			} else {
				//System.out.println("uh oh");
			}
		}
	}
}
