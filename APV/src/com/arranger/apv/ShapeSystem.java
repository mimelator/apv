package com.arranger.apv;

/**
 * Base class for Shape Systems like particle, et al
 */
public abstract class ShapeSystem {

	protected Main parent;
	protected ShapeFactory factory;
	
	public ShapeSystem(Main parent, ShapeFactory factory) {
		this.parent = parent;
		this.factory = factory;
	}
	
	/**
	 * Use the factory to initialize shapes
	 */
	public abstract void setup();
	
	/**
	 * Render loop
	 */
	public abstract void draw();
}
