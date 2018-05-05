package com.arranger.apv.systems.lifecycle;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.factory.APVShape;
import com.arranger.apv.factory.ShapeFactory;
import com.arranger.apv.factory.APVShape.Data;
import com.arranger.apv.systems.ShapeSystem;

import processing.core.PShape;

/**
 * Manages lifespan, color and stroke and delegates drawing to the {@link LifecycleData#update()} 
 * to change the attributes (eg: location, rotation, scaling) of the Shape
 */
public abstract class LifecycleSystem extends ShapeSystem {

	public static final int DEFAULT_STROKE_WEIGHT = 1;
	public static final Color DEFAULT_STROKE_COLOR = Color.BLACK;
	
	protected List<APVShape> particles = new ArrayList<APVShape>();
	protected PShape groupShape = null;
	private int numParticles;
	private int alpha;
	
	protected abstract LifecycleData createData();
	
	public LifecycleSystem(Main parent, ShapeFactory factory, int numParticles) {
		super(parent, factory);
		this.numParticles = numParticles;
		
		alpha = parent.getDefaultShapeSystemAlpha();
	}
	
	@Override
	public String getConfig() {
		//{WarpSystem : [{SpriteFactory : [triangle.png, 2.5]}, ${ALL_PARTICLES}]}
		String result = null;
		if (factory != null) {
			String childConfig = factory.getConfig();
			result = String.format("{%s : [%s, %s]}", getName(), childConfig, numParticles);
		} else {
			result = String.format("{%s : [%s]}", getName(), numParticles);
		}
		return result;
	}

	@Override
	public void setup() {
		
	}
	

	@Override
	public void onFactoryUpdate() {
		particles.clear();
	}

	protected void _setup() {
		groupShape = parent.createShape(PShape.GROUP);
		for (int i = 0; i < numParticles; i++) {
			APVShape s = factory.createShape(createData());
			particles.add(s);
			groupShape.addChild(s.getShape());
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
			_setup();
		}

		int targetNumShapes = getTargetParticles();
		
		//remove the dead
		Iterator<APVShape> iterator = particles.iterator();
		while (iterator.hasNext()) {
			APVShape next = iterator.next();
			LifecycleData d = (LifecycleData)next.getData();
			if (d.isDead()) {
				
				if (particles.size() <= targetNumShapes  && d.isAllowRespawn()) {
					d.respawn();
				} else {
					//Maybe i should store the index?
					int childIndex = next.getShape().getParent().getChildIndex(next.getShape());
					groupShape.removeChild(childIndex);
					iterator.remove();
				}
			}
		}
		
		//add any missing
		while (particles.size() <= targetNumShapes) {
			APVShape s = factory.createShape(createData());
			particles.add(s);
			groupShape.addChild(s.getShape());
		}
		
		//update
		for (APVShape p : particles) {
			((LifecycleData)p.getData()).update();
		}
		
		parent.shape(groupShape);
	}

	protected int getTargetParticles() {
		float result = numParticles * parent.getParticles().getPct();
		return (int)result;
	}
	
	public class LifecycleData extends Data {
		
		public static final int LIFESPAN = 255;
		
		protected float lifespan = LIFESPAN;
		protected Color color = Color.WHITE;
		protected boolean allowRespawn = true;
		
		public LifecycleData() {
			respawn();
			lifespan = parent.random(LIFESPAN);
		}
		
		public boolean isAllowRespawn() {
			return allowRespawn;
		}

		public void setAllowRespawn(boolean allowRespawn) {
			this.allowRespawn = allowRespawn;
		}

		/**
		 * {@link LifecycleSystem#draw()}
		 * Every Draw cycle will give each LifecycleData the chance to update it's attributes
		 * This Lifecyle Data will also check for "death" 
		 */
		public void update() {
			lifespan--;
			
			//update color
			int result = parent.color(color.getRed(), color.getGreen(), color.getBlue(), lifespan);
			shape.setColor(result, alpha);
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
			color = parent.getColor().getCurrentColor();
		}
	}
}
