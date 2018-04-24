package com.arranger.apv;

import com.arranger.apv.factory.APVShape;
import com.arranger.apv.factory.APVShape.Data;

/**
 * Responsible for creating shapes
 */
public abstract class ShapeFactory extends APVPlugin {

	public static final float DEFAULT_SCALE = 1.0f;
	
	protected float scale = DEFAULT_SCALE;
	
	public ShapeFactory(Main parent) {
		super(parent);
	}
	
	public ShapeFactory(Main parent, float scale) {
		super(parent);
		this.scale = scale;
	}
	
	public float getScale() {
		return scale;
	}
	
	public abstract APVShape createShape(Data data);
}
