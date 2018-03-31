package com.arranger.apv.loc;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.factories.PrimitiveShapeFactory;

import processing.core.PApplet;

public class CircularLocationSystem extends LocationSystem {
	
	private static final int LOOP_IN_SECONDS = 5;
	private static final float SCALE = .75f;
	protected Point2D[] points;
	protected int startTime;
	
	public CircularLocationSystem(Main parent) {
		super(parent);
		
		float width = parent.width * SCALE;
		float height = parent.height * SCALE;
		float x = parent.width * (1 - SCALE) / 2;
		float y = parent.height * (1 - SCALE) / 2;
		
		points = PrimitiveShapeFactory.flattenShape(new Ellipse2D.Float(x, y, width, height));
		startTime = parent.millis();
	}
	
	public Point2D getCurrentPoint() {
		int millisEllapsed = parent.millis() - startTime;
		float secEllapsed = millisEllapsed / 1000.f;
		secEllapsed %= LOOP_IN_SECONDS; 
		float pct = secEllapsed / LOOP_IN_SECONDS;
		
		//iterate through the points every 10 seconds
		int result = (int)PApplet.lerp(0, points.length - 1, pct);
		return points[result % points.length];
	}
}