package com.arranger.apv.systems;

import java.awt.Color;
import java.awt.geom.Point2D;
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
 * For each APVShape, the Particle System manages:
 *	
 *  LifeSpan
 *  Gravity
 *  Translation
 *  Alpha Adjust
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
		for (APVShape p : particles) {
			((ParticleData)p.getData()).update();
		}
		parent.shape(groupShape);
	}
	
	protected class ParticleData extends Data {
		private static final float DEFAULT_GRAVITY = 0.1f;
		private static final int LIFESPAN = 255;
		
		private float lifespan = LIFESPAN;
		private PVector gravity = new PVector(0, DEFAULT_GRAVITY);
		private PVector velocity;
		private Color color = Color.WHITE;
		
		public ParticleData() {
			rebirth(0, 0);
			lifespan = parent.random(LIFESPAN);
		}
		
		public void update() {
			if (--lifespan < 0) {
				Point2D p = parent.getLocationSystem().getCurrentPoint();
				rebirth((float)p.getX(), (float)p.getY());
			}
			
			gravity.y = parent.getGravity().getCurrentGravity();
			velocity.add(gravity);

			//lifespan changes the alpha 
			int result = parent.color(color.getRed(), color.getGreen(), color.getBlue(), lifespan);
			shape.setColor(result);
			shape.getShape().translate(velocity.x, velocity.y);
		}
		
		private void rebirth(float x, float y) {
			float a = parent.random(PApplet.TWO_PI);
			float speed = parent.random(0.5f, 4);
			velocity = new PVector(PApplet.cos(a), PApplet.sin(a));
			velocity.mult(speed);
			lifespan = LIFESPAN;
			
			if (shape != null && shape.getShape() != null) {
				PShape pShape = shape.getShape();
				pShape.resetMatrix();
				pShape.translate(x, y);
			}
			color = parent.getColorSystem().getCurrentColor();
		}
	}
}
