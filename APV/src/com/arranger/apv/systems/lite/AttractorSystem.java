package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.APVShape;
import com.arranger.apv.FrameSkipper;
import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;
import com.arranger.apv.audio.PulseListener;

import processing.core.PApplet;

/**
 * https://www.openprocessing.org/sketch/502489
 */
public class AttractorSystem extends LiteShapeSystem {

	private static final int LARGE_MAGNETISM = 50;
	private static final int SMALL_MAGNETISM = 7;
	private static final int MAGNETISM_OSCILATOR = 4;
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
	
	private PulseListener pulseListener;
	private FrameSkipper frameSkipper;
	private boolean reverse = true;
	private APVShape shape = null;

	public AttractorSystem(Main parent) {
		super(parent);
	}
	
	public AttractorSystem(Main parent, ShapeFactory shapeFactory) {
		super(parent);
		this.factory = shapeFactory;
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
		
		parent.getCommandSystem().registerCommand('r', "Reverse Path", "Changes the direction of the path", event -> this.reverse = !reverse);
		
		if (factory != null) {
			shape = factory.createShape(null);
		}
		
		pulseListener = new PulseListener(parent, 1, 8); 
		frameSkipper = new FrameSkipper(parent);
	}

	@Override
	public void draw() {
		if (frameSkipper.isNewFrame() && pulseListener.isNewPulse()) {
			reverse = !reverse;
		}
		
		if (parent.frameCount % RESET_FRAMES == 0) {
			setup();
		}
		
		radius = parent.oscillate(SMALL_RADIUS, LARGE_RADIUS, RADIUS_OSCILATOR);
		magnetism = parent.oscillate(SMALL_MAGNETISM, LARGE_MAGNETISM, MAGNETISM_OSCILATOR);
		
		parent.addDebugMsg("  --magnetism: " + magnetism);
		parent.addDebugMsg("  --radius: " + radius);
		parent.addDebugMsg("  --reverse: " + reverse);
		parent.noStroke();

		Color c = parent.getColorSystem().getCurrentColor();
		Point2D p = parent.getLocationSystem().getCurrentPoint();
		int mouseX = (int)p.getX();
		int mouseY = (int)p.getY();
		
		for (int i = 0; i < NUM_SHAPES; i++) {
			float distance = PApplet.dist(mouseX, mouseY, x[i], y[i]); 
			
			float tempMagnetism = (reverse) ? -magnetism : magnetism;

			if (distance > 3) {
				ax[i] = tempMagnetism * (mouseX - x[i]) / (distance * distance);
				ay[i] = tempMagnetism * (mouseY - y[i]) / (distance * distance);
			}
			vx[i] += ax[i];
			vy[i] += ay[i];

			vx[i] = vx[i] * gensoku;
			vy[i] = vy[i] * gensoku;

			x[i] += vx[i];
			y[i] += vy[i];
			
			if (shape != null) {
				shape.setColor(c.getRGB());
				parent.shape(shape.getShape(), x[i], y[i]);
			} else {
				int sokudo = (int) PApplet.dist(0, 0, vx[i], vy[i]);
				int r = (int) PApplet.map(sokudo, 0, DISTANCE_FOR_COLOR, 0, c.getRed());
				int g = (int) PApplet.map(sokudo, 0, DISTANCE_FOR_COLOR, 0, c.getGreen());
				int b = (int) PApplet.map(sokudo, 0, DISTANCE_FOR_COLOR, 0, c.getBlue());
				parent.fill(r, g, b);
				parent.ellipse(x[i], y[i], radius, radius);
			}
		}
	}

}
