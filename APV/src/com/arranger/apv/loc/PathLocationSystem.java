package com.arranger.apv.loc;

import java.awt.Shape;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.factories.PrimitiveShapeFactory;

import processing.core.PApplet;
import processing.event.KeyEvent;

public abstract class PathLocationSystem extends LocationSystem {

	protected Point2D[] points;
	protected int secondsPerPath; 
	private int startTime;
	private boolean reverse = false;
	
	public PathLocationSystem(Main parent, int secondsPerPath) {
		super(parent);
		this.secondsPerPath = secondsPerPath;
		points = PrimitiveShapeFactory.flattenShape(createPath());
		startTime = parent.millis();
		parent.registerMethod("keyEvent", this);
	}
	
	public void keyEvent(KeyEvent keyEvent) {
		if (keyEvent.getAction() == KeyEvent.RELEASE) {
			int keyCode = keyEvent.getKeyCode();
			if (keyCode == 'r' || keyCode == 'R') {
				reverse = !reverse;
			}
		}
	}
	
	protected abstract Shape createPath();
	
	public Point2D getCurrentPoint() {
		float pct = getPercentagePathComplete();
		float result = PApplet.lerp(0, points.length - 1, pct);

		int indexFloor = (int)Math.floor(result);
		int indexCeil = (int)Math.ceil(result);
		float pct2 = result - indexFloor;
		
		float x1 = (float)points[indexFloor].getX();
		float y1 = (float)points[indexFloor].getY();
		float x2 = (float)points[indexCeil].getX();
		float y2 = (float)points[indexCeil].getY();
		
		//try to do further interpoloation
		float x = PApplet.lerp(x1, x2, pct2);
		float y = PApplet.lerp(y1, y2, pct2);
		
		return new Point2D.Float(x, y);
	}

	protected float getPercentagePathComplete() {
		int millisEllapsed = parent.millis() - startTime;
		float secEllapsed = millisEllapsed / 1000.f;
		secEllapsed %= secondsPerPath; 
		float result = secEllapsed / secondsPerPath;
		if (result < 0.0f || result > 1.0f) {
			throw new RuntimeException("Illegal value for pct: " + result);
		}
		
		if (reverse) {
			return 1.0f - result;
		} else {
			return result;
		}
	}

}
