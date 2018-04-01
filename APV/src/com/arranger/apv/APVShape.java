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
	
	public void resetMatrix() {
		getShape().resetMatrix();
	}
	
	public void translate(float x, float y) {
		getShape().translate(x, y);
	}
	
	public void rotate(float radians) {
		getShape().rotate(radians);
	}
	
	public Data getData() {
		return data;
	}
	
	public void setColor(int color) {
		getShape().setFill(color);
	}

	protected abstract PShape createNewShape();

	public static abstract class Data {
		protected APVShape shape;
	}
}
