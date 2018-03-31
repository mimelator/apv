package com.arranger.apv;

import processing.core.PShape;

public abstract class APVShape {

	protected Main parent;
	protected Data data;
	protected PShape shape;
	
	public APVShape(Main parent, Data data) {
		this.parent = parent;
		this.data = data;
		this.data.shape = this;
	}
	
	public PShape getShape() {
		if (shape == null) {
			shape = createNewShape();
		}
		return shape;
	}
	
	public Data getData() {
		return data;
	}

	protected abstract PShape createNewShape();

	public static abstract class Data {
		protected APVShape shape;
	}
}
