package com.arranger.apv.systems;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.factory.ShapeFactory;

/**
 * Base class for Shape Systems like particle, et al
 */
public abstract class ShapeSystem extends APVPlugin {

	protected ShapeFactory factory;
	
	public ShapeSystem(Main parent, ShapeFactory factory) {
		super(parent);
		setShapeFactory(factory);
	}

	protected void setShapeFactory(ShapeFactory factory) {
		this.factory = factory;
		if (factory != null) {
			factory.setShapeSystem(this);
		}
	}
	
	/**
	 * Render loop
	 */
	public abstract void draw();
	
	public abstract void onFactoryUpdate();
	
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
