package com.arranger.apv;

import processing.core.PShape;

/**
 * This class is a wrapper around a PShape
 */
public abstract class APVShape {

	protected Main parent;
	protected Data data;
	protected PShape shape;
	
	public APVShape(Main parent, Data data) {
		this.parent = parent;
		this.data = data;
		if (data != null) {
			this.data.shape = this;
		}
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
	
	public void scale(float scale) {
		getShape().scale(scale);
	}
	
	public Data getData() {
		return data;
	}
	
	public void setColor(int color) {
		getShape().setFill(color);
	}
	
	public void setColor(int color, float alpha) {
		getShape().setFill(parent.color(color, alpha));
	}
	
	public void centerShape() {
		getShape().translate(parent.height / 2, parent.width / 2); 
	}

	protected abstract PShape createNewShape();

	public static abstract class Data {
		protected APVShape shape;
	}
}
