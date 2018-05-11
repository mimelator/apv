package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.factory.APVShape;
import com.arranger.apv.factory.ShapeFactory;
import com.arranger.apv.util.Configurator;

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
	
	private float[] vx = null;
	private float[] vy = null;
	private float[] x = null;
	private float[] y = null;
	private float[] ax = null;
	private float[] ay = null;

	private float magnetism;
	private float radius;
	private float gensoku = 0.95f;
	
	private boolean reverse = true;
	private APVShape shape = null;
	private int numShapes = NUM_SHAPES;

	public AttractorSystem(Main parent) {
		super(parent);
	}
	
	public AttractorSystem(Main parent, ShapeFactory shapeFactory) {
		this(parent, shapeFactory, NUM_SHAPES);
	}
	
	public AttractorSystem(Main parent, ShapeFactory shapeFactory, int numShapes) {
		super(parent);
		this.factory = shapeFactory;
		if (this.factory != null) {
			this.factory.setShapeSystem(this);
		}
		this.numShapes = numShapes;
	}

	public AttractorSystem(Configurator.Context ctx) {
		this(ctx.getParent(), (ShapeFactory) ctx.loadPlugin(0), ctx.getInt(1, NUM_SHAPES));
	}
	
	@Override
	public void setup() {
		
		vx = new float[numShapes];
		vy = new float[numShapes];
		x = new float[numShapes];
		y = new float[numShapes];
		ax = new float[numShapes];
		ay = new float[numShapes];

		
		for (int i = 0; i < numShapes; i++) {
			x[i] = random(parent.width);
			y[i] = random(parent.height);
			vx[i] = 0;
			vy[i] = 0;
			ax[i] = 0;
			ay[i] = 0;
		}
		
		parent.getCommandSystem().registerHandler(Command.REVERSE, (command, source, modifiers) -> this.reverse = !reverse);
		
		if (factory != null) {
			shape = factory.createShape(null);
		}
	}
	
	@Override
	public void onFactoryUpdate() {
		super.onFactoryUpdate();
		shape = factory.createShape(null);
	}

	@Override
	public void draw() {
		if (parent.getFrameCount() % RESET_FRAMES == 0) {
			setup();
		}
		
		radius = parent.oscillate(SMALL_RADIUS, LARGE_RADIUS, RADIUS_OSCILATOR);
		magnetism = parent.oscillate(SMALL_MAGNETISM, LARGE_MAGNETISM, MAGNETISM_OSCILATOR);
		
		parent.addSettingsMessage("  --magnetism: " + magnetism);
		parent.addSettingsMessage("  --radius: " + radius);
		parent.addSettingsMessage("  --reverse: " + reverse);
		
		parent.noStroke();

		Color c = parent.getColor().getCurrentColor();
		Point2D p = parent.getCurrentPoint();
		int mouseX = (int)p.getX();
		int mouseY = (int)p.getY();
		
		for (int i = 0; i < numShapes; i++) {
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
