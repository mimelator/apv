package com.arranger.apv.systems.lite;

import com.arranger.apv.Main;
import com.arranger.apv.systems.ShapeSystem;

import processing.core.PApplet;


/**
 * This is the next version of shape systems that are a bit more optimized and therefore 'lightweight'
 */
public abstract class LiteShapeSystem extends ShapeSystem {

	public LiteShapeSystem(Main parent) {
		super(parent, null);
	}

	protected float cos(float theta) {
		return PApplet.cos(theta);
	}
	
	protected float sin(float theta) {
		return PApplet.sin(theta);
	}
	
	protected float random(float high) {
		return parent.random(high);
	}
	
	protected float random(float low, float high) {
		return parent.random(low, high);
	}

	@Override
	public void onFactoryUpdate() {
		//Most instances of LiteShapeSystem don't use a ShapeFactory
	}
}
