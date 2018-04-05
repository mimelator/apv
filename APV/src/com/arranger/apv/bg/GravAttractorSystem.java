package com.arranger.apv.bg;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;

import processing.core.PApplet;
import processing.core.PVector;

/*
 * https://www.openprocessing.org/sketch/472869
 */
public class GravAttractorSystem extends BackDropSystem {

	@Override
	public void drawBackground() {
		Point2D currentPoint = parent.getLocationSystem().getCurrentPoint();
		center_gravity.x = (float) currentPoint.getX();
		center_gravity.y = (float) currentPoint.getY();
		move();
		view();
	}

	private static final int NUM_POINTS = 32;

	PVector[] points1 = new PVector[NUM_POINTS];
	PVector[] acceleration1 = new PVector[NUM_POINTS];
	PVector[] velocity1 = new PVector[NUM_POINTS];

	PVector[] points2 = new PVector[NUM_POINTS];
	PVector[] points3 = new PVector[NUM_POINTS];
	PVector[] points4 = new PVector[NUM_POINTS];

	float topspeed;
	float pointsdimension;
	PVector center_gravity;

	boolean hmirror = false;
	boolean vmirror = false;

	int width = parent.width;
	int height = parent.height;

	boolean swtichColorsQuick = true;
	Color currentColor;
	
	public GravAttractorSystem(Main parent) {
		super(parent);
		reset();
	}

	private void view() {
		parent.blendMode(MULTIPLY);
	
		currentColor = (swtichColorsQuick) ? parent.getColorSystem().getCurrentColor() : currentColor;
		parent.stroke(currentColor.getRGB());
		
		for (int i = 0; i < points1.length; i++) {
			
			parent.point(points1[i].x, points1[i].y);
			for (int j = 0; j < points1.length; j++) {
				if (PApplet.dist(points1[i].x, points1[i].y, points1[j].x, points1[j].y) < width / 12) {
					parent.line(points1[i].x, points1[i].y, points1[j].x, points1[j].y);
				}

				if (PApplet.dist(points2[i].x, points2[i].y, points2[j].x, points2[j].y) < width / 12 && hmirror) {
					parent.line(points2[i].x, points2[i].y, points2[j].x, points2[j].y);
				}

				if (PApplet.dist(points3[i].x, points3[i].y, points3[j].x, points3[j].y) < width / 12 && vmirror) {
					parent.line(points3[i].x, points3[i].y, points3[j].x, points3[j].y);
				}

				if (PApplet.dist(points4[i].x, points4[i].y, points4[j].x, points4[j].y) < width / 12 && hmirror
						&& vmirror) {
					parent.line(points4[i].x, points4[i].y, points4[j].x, points4[j].y);
				}
			}
			
			parent.blendMode(BLEND);
		}
	}

	private void move() {
		for (int i = 0; i < NUM_POINTS; i++) {
			acceleration1[i] = PVector.sub(center_gravity, points1[i]);
			acceleration1[i].normalize();
			acceleration1[i].mult(5);
			velocity1[i].add(acceleration1[i]);
			velocity1[i].limit(topspeed);
			points1[i].add(velocity1[i]);
			points2[i].set(width - points1[i].x, points1[i].y);
			points3[i].set(points1[i].x, height - points1[i].y);
			points4[i].set(width - points1[i].x, height - points1[i].y);
		}
	}

	private void reset() {
		topspeed = 10 + (float) Math.random() * 20;
		pointsdimension = 0.5f + (float) Math.random();
		center_gravity = new PVector(
				(float) parent.random(width * 0.3f, width * 0.7f),
				(float) parent.random(height * 0.3f, height * 0.7f));

		for (int i = 0; i < NUM_POINTS; i++) {
			points1[i] = new PVector((float) Math.random() * width, (float) Math.random() * height);
			acceleration1[i] = new PVector(0, 0);
			velocity1[i] = new PVector(0, 0);
			points2[i] = new PVector(width - points1[i].x, points1[i].y);
			points3[i] = new PVector(points1[i].x, height - points1[i].y);
			points4[i] = new PVector(width - points1[i].x, height - points1[i].y);
		}
		currentColor = parent.getColorSystem().getCurrentColor();
	}
}
