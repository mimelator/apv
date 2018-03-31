package com.arranger.apv.systems;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVShape;
import com.arranger.apv.APVShape.Data;
import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;
import com.arranger.apv.ShapeSystem;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

/**
 * For each APVShape, the Particle System should manage:
 *	
 *  LifeSpan
 *  Gravity
 *  Translation
 *  Alpha Adjust
 *  
 *  I don't want the APVShape to have to know about any of the four above topics.
 *  Those should just be manged by this ParticleSystem.  However, I need to attach
 *  data to each of the APVShapes
 */
public class ParticleSystem extends ShapeSystem {

	private List<APVShape> particles = new ArrayList<APVShape>();
	private PShape groupShape;
	private int numParticles;
	
	
	public ParticleSystem(Main parent, ShapeFactory factory, int numParticles) {
		super(parent, factory);
		this.numParticles = numParticles;
	}

	@Override
	public void setup() {
		groupShape = parent.createShape(PShape.GROUP);
		for (int i = 0; i < numParticles; i++) {
			APVShape s = factory.createShape(new ParticleData());
			particles.add(s);
			groupShape.addChild(s.getShape());
		}
	}

	@Override
	public void draw() {
		//update and reset any particles
		for (APVShape p : particles) {
			((ParticleData)p.getData()).update();
		}
		
		parent.shape(groupShape);
	}
	
	protected class ParticleData extends Data {
		//lifespan and gravity
		private static final float DEFAULT_GRAVITY = 0.1f;
		private static final int LIFESPAN = 255;
		
		private float lifespan = LIFESPAN;
		private PVector gravity = new PVector(0, DEFAULT_GRAVITY);
		private PVector velocity;
		private Color color = Color.WHITE; //TODO Externalize COLOR!!!!
		
		public ParticleData() {
			super();
			rebirth(parent.width / 2, parent.height / 2);
			lifespan = parent.random(LIFESPAN);
		}
		
		public void update() {
			lifespan = lifespan - 1;
			if (lifespan < 0) {
				rebirth(parent.mouseX, parent.mouseY); //TODO Externalize Location Logic
			}
			
			gravity.y = parent.getGravity().getCurrentGravity();
			velocity.add(gravity);

			//lifespan changes the alpha of the color
			PShape pShape = this.shape.getShape();
			int result = parent.color(color.getRed(), color.getGreen(), color.getBlue(), lifespan);
			pShape.setFill(result);
			pShape.translate(velocity.x, velocity.y);
		}
		
		private void rebirth(float x, float y) {
			float a = parent.random(PApplet.TWO_PI);
			float speed = parent.random(0.5f, 4);
			velocity = new PVector(PApplet.cos(a), PApplet.sin(a));
			velocity.mult(speed);
			lifespan = LIFESPAN;
			
			if (this.shape != null && this.shape.getShape() != null) {
				PShape pShape = this.shape.getShape();
				pShape.resetMatrix();
				pShape.translate(x, y);
			}
			//color = parent.getBeatInfo().getCurrentColor();
		}
	}
}
