package com.arranger.apv.systems.lifecycle;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;
import com.arranger.apv.util.Configurator;

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
	
	public GravitySystem(Configurator.Context ctx) {
		this(ctx.getParent(), 
				(ShapeFactory)ctx.loadPlugin(0), 
				ctx.getInt(1, Main.NUMBER_PARTICLES));
	}
	
	
	@Override
	public void draw() {
		super.draw();
		parent.addSettingsMessage("Gravity: " + parent.getGravity().getCurrentGravity());
	}

	@Override
	protected LifecycleData createData() {
		return new GravityData();
	}

	/**
	 * Delegate to the LocationSystem
	 */
	@Override
	public String getDisplayName() {
		return super.getDisplayName() + "[" + parent.getLocation().getDisplayName() + "]";
	}

	protected class GravityData extends LifecycleData {
		private static final int HIGH_SPEED_RANGE = 4;
		private static final float LOW_SPEED_RANGE = 0.5f;
		private static final float DEFAULT_GRAVITY = 0.1f;
		
		protected PVector gravity = new PVector(0, DEFAULT_GRAVITY);
		protected PVector velocity;
		private boolean isInit = false;

		/**
		 * {@link GravitySystem#draw()}
		 */
		public void update() {
			if (!isInit) {
				setInitialLocation();
			}
			super.update();
			
			updateGravity();
			updateLocation();
		}
		
		/**
		 * called from {@link #update()} and changes the location based on {@link #velocity}
		 */
		protected void updateLocation() {
			shape.translate(velocity.x, velocity.y);
			if (shape.isOffscreen()) {
				lifespan = -1; //kill it
			}
		}

		/**
		 * called from {@link #update()} and changes the gravity based on the global gravity 
		 */
		protected void updateGravity() {
			gravity.y = parent.getGravity().getCurrentGravity();
			velocity.add(gravity);
		}

		/**
		 * called when {@link #isDead()} is true during the update/draw phase
		 * Also called during {@link GravitySystem#setup()} / {@link #setShape(com.arranger.apv.APVShape)}
		 */
		protected void respawn() {
			super.respawn();
			
			//pick a direction
			float a = parent.random(PApplet.TWO_PI);
			velocity = new PVector(PApplet.cos(a), PApplet.sin(a));

			//get the speed
			float speed = parent.random(LOW_SPEED_RANGE, HIGH_SPEED_RANGE);
			velocity.mult(speed);

			//Set initial location
			setInitialLocation();
		}

		/**
		 * called during {@link #respawn()}
		 */
		protected void setInitialLocation() {
			Point2D p = parent.getLocation().getCurrentPoint();
			if (shape != null && shape.getShape() != null) {
				shape.resetMatrix();
				shape.rotate(PApplet.radians(parent.random(360)));
				shape.translate((float)p.getX(), (float)p.getY());
				isInit = true;
			}
		}
	}
}
