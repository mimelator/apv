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

import processing.core.PShape;

/**
 * For each APVShape manage:
 *	
 *  lifespan
 */
public abstract class LifecycleSystem extends ShapeSystem {

	private List<APVShape> particles = new ArrayList<APVShape>();
	private PShape groupShape;
	private int numParticles;
	
	protected abstract LifecycleData createData();
	
	public LifecycleSystem(Main parent, ShapeFactory factory, int numParticles) {
		super(parent, factory);
		this.numParticles = numParticles;
	}

	@Override
	public void setup() {
		groupShape = parent.createShape(PShape.GROUP);
		for (int i = 0; i < numParticles; i++) {
			APVShape s = factory.createShape(createData());
			particles.add(s);
			groupShape.addChild(s.getShape());
		}
	}

	@Override
	public void draw() {
		for (APVShape p : particles) {
			((LifecycleData)p.getData()).update();
		}
		parent.shape(groupShape);
	}

	protected class LifecycleData extends Data {
		
		private static final int LIFESPAN = 255;
		protected float lifespan = LIFESPAN;
		protected Color color = Color.WHITE;
		
		public LifecycleData() {
			rebirth(0, 0);
			lifespan = parent.random(LIFESPAN);
		}
		
		public void update() {
			if (--lifespan < 0) {
				Point2D p = parent.getLocationSystem().getCurrentPoint();
				rebirth((float)p.getX(), (float)p.getY());
			}
			
			//lifespan changes the alpha 
			int result = parent.color(color.getRed(), color.getGreen(), color.getBlue(), lifespan);
			shape.setColor(result);
		}
		
		protected void rebirth(float x, float y) {
			lifespan = LIFESPAN;
			color = parent.getColorSystem().getCurrentColor();
		}
	}
	
}
