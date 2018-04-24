package com.arranger.apv.factory;

import com.arranger.apv.Main;

import processing.core.PShape;

/**
 * This class is a wrapper around a PShape
 */
public abstract class APVShape {

	protected Main parent;
	protected Data data;
	protected PShape shape;
	private float translateX = 0, translateY = 0;
	
	public APVShape(Main parent, Data data) {
		this.parent = parent;
		this.data = data;
		if (data != null) {
			this.data.shape = this;
		}
	}
	
	public float getWidth() {
		PShape s = getShape();
		return s.getWidth();
	}
	
	public float getHeight() {
		PShape s = getShape();
		return s.getHeight();
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
	
	public boolean isOffscreen() {
		if (translateX < 0 || translateY < 0) {
			return true;
		} else if (translateX > parent.width || translateY > parent.height) {
			return true;
		} else {
			return false;
		}
	}
	
	public void translate(float x, float y) {
		translateX += x;
		translateY += y;
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
	
	protected abstract PShape createNewShape();

	public static abstract class Data {
		protected APVShape shape;
	}
}
