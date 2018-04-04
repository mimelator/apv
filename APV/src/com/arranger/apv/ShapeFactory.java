package com.arranger.apv;

import com.arranger.apv.APVShape.Data;

/**
 * Responsible for creating shapes
 */
public abstract class ShapeFactory {

	public static final float DEFAULT_SCALE = 1.0f;
	
	protected float scale = DEFAULT_SCALE;
	protected Main parent;
	
	
	public ShapeFactory(Main parent) {
		this.parent = parent;
	}
	
	public ShapeFactory(Main parent, float scale) {
		this.parent = parent;
		this.scale = scale;
	}
	
	public float getScale() {
		return scale;
	}
	
	public abstract APVShape createShape(Data data);
}
