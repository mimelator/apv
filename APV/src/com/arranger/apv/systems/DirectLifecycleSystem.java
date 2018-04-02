package com.arranger.apv.systems;

import com.arranger.apv.APVShape;
import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;

import processing.core.PApplet;
import processing.core.PConstants;

public class DirectLifecycleSystem extends LifecycleSystem implements PConstants{


	public DirectLifecycleSystem(Main parent, ShapeFactory factory, int numParticles) {
		super(parent, factory, numParticles);
	}

	@Override
	protected LifecycleData createData() {
		return new DirectLifecycleData();
	}

	public void draw() {
		super.draw();
		
		parent.noStroke();
		
		for (APVShape p : particles) {
			DirectLifecycleData d = (DirectLifecycleData)p.getData();
			parent.pushMatrix();
			d.update();
			d.draw();
			parent.popMatrix();
		}

		parent.addDebugMsg("WARP " + PApplet.round(warp() * 10) / 10.0);
	}

	private float warp() {
		return parent.oscillate(0, 10);
	}

	protected class DirectLifecycleData extends LifecycleData {
	
		private static final int STAR_HUE = 150;
		private static final int TRANSPARENT = 100;
		protected float angle; //their angle versus the center of the screen
		protected float dist; //distance from center of the screen
		protected float speed; //speed leaving the center
		protected float bright; //brightness (start black, fade to white)
		protected float thick; //diameter of the warp star

		public DirectLifecycleData() {
		}

		protected void draw() {
			parent.translate(parent.width / 2, parent.height / 2);
			parent.rotate(angle);
			parent.translate(dist, 0);
			parent.stroke(starcolor());
			parent.strokeWeight(thick);
			parent.line(0, 0, speed, 0); // draw line from previous to next position
		}
		
		@Override
		public void update() {
			super.update();
			dist += speed; // Move the warp stars
			speed += .1 * warp(); // accelerate as they leave center
			bright += 5;
		}

		private int starcolor() {
			parent.colorMode(PApplet.HSB);
			float sat = PApplet.map(warp(), 1, 10, 0, TRANSPARENT);
			return parent.color(STAR_HUE, sat, bright);
		}

		protected boolean isDead() {
			return dist > parent.width + parent.height;
		}

		protected void rebirth() {
			angle = parent.random(0, 2 * PApplet.PI * 100);
			dist  = parent.random(parent.width / 50, parent.width);
			speed = parent.random(0 * warp(), .1f * warp());
			thick = parent.random(1, 5);
			bright = 0;
		}
	 }
}