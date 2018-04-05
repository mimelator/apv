package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.color.OscillatingColor;

import processing.core.PApplet;

/**
 * Example Array2D from Processing
 */
public class GridShapeSystem extends LiteShapeSystem {

	private static final int COLOR_OSC_SCALAR = 20;
	private static final int LARGE_STROKE_WEIGHT = 300;//30;
	private static final int SPACER = 100;
	
	float[][] distances;
	float maxDistance;
	int space;
	int shapeSize;
	Color col;
	
	
	public GridShapeSystem(Main parent) {
		this(parent, SPACER, LARGE_STROKE_WEIGHT);
	}
		
	public GridShapeSystem(Main parent, int space, int shapeSize) {
		super(parent);
		this.space = space; 
		this.shapeSize = shapeSize;
	}
	
	@Override
	public void setup() {
		distances = new float[parent.width][parent.height];
	}

	@Override
	public void draw() {
		int time = parent.millis();
		parent.stroke(255);
		col = new OscillatingColor(parent, COLOR_OSC_SCALAR).getCurrentColor();
		Point2D pt = parent.getLocationSystem().getCurrentPoint();
		float ptX = (float)pt.getX(); 
		float ptY = (float)pt.getY(); 
		int width = parent.width;
		int height = parent.height;

		maxDistance = PApplet.dist(ptX, ptY, width, height);
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				float distance = PApplet.dist(ptX, ptY, x, y);
				distances[x][y] = distance / maxDistance * 255;
			}
		}

		parent.strokeWeight(shapeSize);
		

		float maxDistance = Float.MIN_VALUE;
		for (int y = 0; y < height; y += space) {
			for (int x = 0; x < width; x += space) {
				float grey = distances[x][y];
				maxDistance = Math.max(maxDistance, grey);
				float alpha = PApplet.map(grey, 0, 255, 0, 255);
				parent.stroke(col.getRed(), col.getGreen(), col.getBlue(), alpha);
				parent.point(x + space / 2, y + space / 2);
			}
		}
		
		parent.addDebugMsg("  --space: " + space); 
		parent.addDebugMsg("  --shapeSize: " + shapeSize);
		parent.addDebugMsg("  --maxDistance: " + maxDistance);
		parent.addDebugMsg("  --drawingTime: " + String.valueOf(parent.millis() - time));
	}

}
