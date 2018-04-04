package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;

import processing.core.PApplet;

/**
 * https://www.openprocessing.org/sketch/502489
 */
public class AttractorSystem extends LiteShapeSystem {

	private static final int LARGE_MAGNETISM = 30;
	private static final int SMALL_MAGNETISM = 7;
	private static final int MAGNETISM_OSCILATOR = 8;
	private static final int RADIUS_OSCILATOR = 5;
	private static final int LARGE_RADIUS = 8;
	private static final int SMALL_RADIUS = 3;
	private static final int DISTANCE_FOR_COLOR = 9;
	
	private static final int NUM_SHAPES = 1000;
	private static final int RESET_FRAMES = 2000;
	
	private float[] vx = new float[NUM_SHAPES];
	private float[] vy = new float[NUM_SHAPES];
	private float[] x = new float[NUM_SHAPES];
	private float[] y = new float[NUM_SHAPES];
	private float[] ax = new float[NUM_SHAPES];
	private float[] ay = new float[NUM_SHAPES];

	private float magnetism;
	private float radius;
	private float gensoku = 0.95f;

	public AttractorSystem(Main parent) {
		super(parent);
	}

	@Override
	public void setup() {
		for (int i = 0; i < NUM_SHAPES; i++) {
			x[i] = random(parent.width);
			y[i] = random(parent.height);
			vx[i] = 0;
			vy[i] = 0;
			ax[i] = 0;
			ay[i] = 0;
		}
	}

	@Override
	public void draw() {
		if (parent.frameCount % RESET_FRAMES == 0) {
			setup();
		}
		
		radius = parent.oscillate(SMALL_RADIUS, LARGE_RADIUS, RADIUS_OSCILATOR);
		magnetism = parent.oscillate(SMALL_MAGNETISM, LARGE_MAGNETISM, MAGNETISM_OSCILATOR);
		
		parent.addDebugMsg("  --magnetism: " + magnetism);
		
		parent.noStroke();
		parent.blendMode(DIFFERENCE);

		Color c = parent.getColorSystem().getCurrentColor();
		Point2D p = parent.getLocationSystem().getCurrentPoint();
		int mouseX = (int)p.getX();
		int mouseY = (int)p.getY();
		
		for (int i = 0; i < NUM_SHAPES; i++) {
			float distance = PApplet.dist(mouseX, mouseY, x[i], y[i]); 

			if (distance > 3) {
				ax[i] = magnetism * (mouseX - x[i]) / (distance * distance);
				ay[i] = magnetism * (mouseY - y[i]) / (distance * distance);
			}
			vx[i] += ax[i];
			vy[i] += ay[i];

			vx[i] = vx[i] * gensoku;
			vy[i] = vy[i] * gensoku;

			x[i] += vx[i];
			y[i] += vy[i];

			int sokudo = (int) PApplet.dist(0, 0, vx[i], vy[i]);
//			int r = (int) PApplet.map(sokudo, 0, DISTANCE_FOR_COLOR, 0, 255);
//			int g = (int) PApplet.map(sokudo, 0, DISTANCE_FOR_COLOR, 64, 255);
//			int b = (int) PApplet.map(sokudo, 0, DISTANCE_FOR_COLOR, 128, 255);
			
			int r = (int) PApplet.map(sokudo, 0, DISTANCE_FOR_COLOR, 0, c.getRed());
			int g = (int) PApplet.map(sokudo, 0, DISTANCE_FOR_COLOR, 0, c.getGreen());
			int b = (int) PApplet.map(sokudo, 0, DISTANCE_FOR_COLOR, 0, c.getBlue());
			
			float alpha = 255;//PApplet.map(sokudo, 0, DISTANCE_FOR_COLOR, 255, 100);
			parent.fill(r, g, b, alpha);
			parent.ellipse(x[i], y[i], radius, radius);
		}
	}

}
