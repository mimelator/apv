package com.arranger.apv.systems;

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
 * Manages lifespan (and color) and delegates drawing to the {@link LifecycleData#update()} 
 * to change the attributes (eg: location, rotation, scaling) of the Shape
 */
public abstract class LifecycleSystem extends ShapeSystem {

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
		
		public static final int LIFESPAN = 255;
		protected float lifespan = LIFESPAN;
		protected Color color = Color.WHITE;
		
		public LifecycleData() {
			respawn();
			lifespan = parent.random(LIFESPAN);
		}
		
		/**
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
	
		protected boolean isDead() {
			return lifespan < 0;
		}
		
		/**
		 * Resets lifespan and color
		 */
		protected void respawn() {
			lifespan = LIFESPAN;
			color = parent.getColorSystem().getCurrentColor();
		}
	}
}
