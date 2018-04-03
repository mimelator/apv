package com.arranger.apv.systems.lifecycle;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVShape;
import com.arranger.apv.APVShape.Data;
import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;
import com.arranger.apv.ShapeSystem;

import processing.core.PShape;

/**
 * Manages lifespan, color and stroke and delegates drawing to the {@link LifecycleData#update()} 
 * to change the attributes (eg: location, rotation, scaling) of the Shape
 */
public abstract class LifecycleSystem extends ShapeSystem {

	public static final int DEFAULT_STROKE_WEIGHT = 1;
	public static final Color DEFAULT_STROKE_COLOR = Color.BLACK;
	
	protected List<APVShape> particles = new ArrayList<APVShape>();
	protected PShape groupShape;
	protected int numParticles;
	
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
			
			
			//setInitialStroke(pShape);
			
		}
	}

	/**
	 * called during {@link #setup()} to set the stroke of the PShape
	 */
	protected void setInitialStroke(PShape pShape) {
		pShape.setStroke(DEFAULT_STROKE_COLOR.getRGB());
		pShape.setStrokeWeight(DEFAULT_STROKE_WEIGHT);
	}

	@Override
	public void draw() {
		if (particles.isEmpty()) {
			setup();
		}
		
		for (APVShape p : particles) {
			((LifecycleData)p.getData()).update();
		}
		parent.shape(groupShape);
	}

	protected class LifecycleData extends Data {
		
		public static final int LIFESPAN = 255;
		
		protected float lifespan = LIFESPAN;
		protected Color color = Color.WHITE;
		
		public LifecycleData() {
			respawn();
			lifespan = parent.random(LIFESPAN);
		}
		
		/**
		 * {@link LifecycleSystem#draw()}
		 * Every Draw cycle will give each LifecycleData the chance to update it's attributes
		 * This Lifecyle Data will also check for "death" and respawn
		 */
		public void update() {
			lifespan--;
			if (isDead()) {
				respawn();
			}
			
			updateColor();
		}

		/**
		 * called from {@link #update()} and changes the alpha based on lifespan 
		 */
		protected void updateColor() {
			int result = parent.color(color.getRed(), color.getGreen(), color.getBlue(), lifespan);
			shape.setColor(result);
		}
	
		/**
		 * checks to see whether lifespan has expired
		 * called from {@link #update()}
		 */
		protected boolean isDead() {
			return lifespan < 0;
		}
		
		/**
		 * Resets lifespan and color
		 * called from {@link #update()} when {@link #isDead()} is true
		 */
		protected void respawn() {
			lifespan = LIFESPAN;
			color = parent.getColorSystem().getCurrentColor();
		}
	}
}
