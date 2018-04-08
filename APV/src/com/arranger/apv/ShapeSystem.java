package com.arranger.apv;

import com.arranger.apv.Main.EmptyShapeFactory;

/**
 * Base class for Shape Systems like particle, et al
 */
public abstract class ShapeSystem extends APVPlugin {

	protected ShapeFactory factory;
	
	public ShapeSystem(Main parent, ShapeFactory factory) {
		super(parent);
		this.factory = factory;
	}
	
	/**
	 * called once
	 */
	public abstract void setup();
	
	/**
	 * Render loop
	 */
	public abstract void draw();

	@Override
	public String getName() {
		if (factory == null || factory instanceof EmptyShapeFactory) {
			return super.getName();
		} else {
			return super.getName() + ":" + factory.getName();
			
		}
	}
}
