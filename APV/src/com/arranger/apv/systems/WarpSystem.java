package com.arranger.apv.systems;

import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;

import processing.core.PApplet;

public class WarpSystem extends DirectLifecycleSystem {

	private static final int MIN_WARP = 0;
	private static final int MAX_WARP = 10;
	
	public WarpSystem(Main parent, ShapeFactory factory, int numParticles) {
		super(parent, factory, numParticles);
	}
	
	@Override
	protected LifecycleData createData() {
		return new WarpData();
	}
	
	@Override
	public void draw() {
		super.draw();
		parent.addDebugMsg("WARP " + PApplet.round(warp() * MAX_WARP) / (double)MAX_WARP);
	}
	
	private float warp() {
		return parent.oscillate(MIN_WARP, MAX_WARP);
	}

	protected class WarpData extends DirectLifecycleData {
		
		protected float angle; //their angle versus the center of the screen
		protected float dist; //distance from center of the screen
		protected float speed; //speed leaving the center
		protected float bright; //brightness (start black, fade to white)
		protected float thick; //diameter of the warp star

		public WarpData() {
		}

		public void draw() {
			parent.translate(parent.width / 2, parent.height / 2);
			parent.rotate(angle);
			parent.translate(dist, 0);
			parent.stroke(color.getRed(), color.getGreen(), color.getBlue(), lifespan);
			parent.strokeWeight(thick);
			parent.line(0, 0, speed, 0); // draw line from previous to next position
		}
		
		@Override
		public void update() {
			super.update();
			dist += speed; 			// Move the warp stars
			speed += .1 * warp(); 	// accelerate as they leave center
			bright += 5;
		}
		
		protected void rebirth() {
			super.rebirth();
			angle = parent.random(0, 2 * PApplet.PI * 100);
			dist  = parent.random(parent.width / 50, parent.width);
			speed = parent.random(0 * warp(), .1f * warp());
			thick = parent.random(1, 5);
			bright = 0;
		}
	 }
}
