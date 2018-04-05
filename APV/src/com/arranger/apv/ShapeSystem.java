package com.arranger.apv;

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
}
