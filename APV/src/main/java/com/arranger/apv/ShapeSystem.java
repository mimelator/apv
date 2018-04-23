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
	 * Render loop
	 */
	public abstract void draw();
	
	public ShapeFactory getFactory() {
		return factory;
	}

	@Override
	public String getConfig() {
		if (factory == null) {
			return super.getConfig();
		}
		
		return String.format("{%s : [%s]}", getName(), factory.getConfig());
	}
}
