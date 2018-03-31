package com.arranger.apv.loc;

import java.awt.Shape;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.factories.PrimitiveShapeFactory;

import processing.core.PApplet;

public abstract class PathLocationSystem extends LocationSystem {

	protected Point2D[] points;
	protected int secondsPerPath; 
	private int startTime;
	
	protected abstract Shape createPath();
	
	public PathLocationSystem(Main parent, int secondsPerPath) {
		super(parent);
		this.secondsPerPath = secondsPerPath;
		points = PrimitiveShapeFactory.flattenShape(createPath());
		startTime = parent.millis();
	}
	
	public Point2D getCurrentPoint() {
		float pct = getPercentagePathComplete();
		int result = (int)PApplet.lerp(0, points.length - 1, pct);
		return points[result % points.length];
	}

	protected float getPercentagePathComplete() {
		int millisEllapsed = parent.millis() - startTime;
		float secEllapsed = millisEllapsed / 1000.f;
		secEllapsed %= secondsPerPath; 
		return secEllapsed / secondsPerPath;
	}
}
