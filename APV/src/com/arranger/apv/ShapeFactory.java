package com.arranger.apv;

import com.arranger.apv.APVShape.Data;

/**
 * Responsible for creating shapes
 */
public abstract class ShapeFactory {

	protected Main parent;
	
	public ShapeFactory(Main parent) {
		this.parent = parent;
	}
	
	public abstract APVShape createShape(Data data);
}
