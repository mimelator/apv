package com.arranger.apv.systems.lite;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.factory.APVShape;
import com.arranger.apv.factory.ShapeFactory;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.frame.Oscillator;
import com.arranger.apv.util.frame.SingleFrameSkipper;

import processing.core.PApplet;

/**
 * @see https://www.openprocessing.org/sketch/492680
 */
public class WanderingInSpace extends LiteShapeSystem {
	
	private static final double ROTATION_SPEED = 0.002;
	private static final int OSC_RATE = 20;
	private static final float HIGH_SPEED_SCALAR = 0.7f;
	private static final float LOW_SPEED_SCALAR = 0.07f;

	Oscillator oscillator;
	SingleFrameSkipper frameSkipper;
	APVShape shape = null;
	
	Particle[] p = new Particle[800];
	int diagonal;
	float rotation = 0;
	
	int oscRate;
	float highSpeedScalar, lowSpeedScalar;
	
	//Dont reverse every time
	int shouldSkip = 2; //Might eventually parameterize
	int skipped = 0;

	public WanderingInSpace(Main parent, ShapeFactory shapeFactory, float lowSpeedScalar, float highSpeedScalar, int oscRate) {
		super(parent);
		this.factory = shapeFactory;
		oscillator = new Oscillator(parent);
		frameSkipper = new SingleFrameSkipper(parent);
		this.oscRate = oscRate;
		this.highSpeedScalar = highSpeedScalar;
		this.lowSpeedScalar = lowSpeedScalar;
	}
	
	public WanderingInSpace(Configurator.Context ctx) {
		this(ctx.getParent(), 
				(ShapeFactory)ctx.loadPlugin(0),
				ctx.getFloat(1, LOW_SPEED_SCALAR),
				ctx.getFloat(2, HIGH_SPEED_SCALAR),
				ctx.getInt(3, OSC_RATE));
	}
	
	@Override
	public String getConfig() {
		//{WanderingInSpace : [.01f, .06f, 20]}
		if (factory != null) {
			return String.format("{%s : [%s, %s, %s, %d]}", getName(), factory.getConfig(), lowSpeedScalar, highSpeedScalar, oscRate);
		} else {
			return String.format("{%s : [{}, %s, %s, %d]}", getName(), lowSpeedScalar, highSpeedScalar, oscRate);
		}
	}

	@Override
	public void setup() {
		for (int i = 0; i < p.length; i++) {
			p[i] = new Particle();
			p[i].o = random(1, random(1, parent.width / p[i].n));
		}

		diagonal = (int) PApplet.sqrt(parent.width * parent.width + parent.height * parent.height) / 2;
		
		if (factory != null) {
			shape = factory.createShape(null);
		}
	}

	protected boolean clockwise = false;
	
	@Override
	public void draw() {
		parent.translate(parent.width / 2, parent.height / 2);
		rotation += (clockwise) ? - ROTATION_SPEED : 0.002;
		parent.rotate(rotation);

		for (int i = 0; i < p.length; i++) {
			p[i].draw();
			if (p[i].drawDist() > diagonal) {
				p[i] = new Particle();
			}
		}
	}
	
	class Particle {
		
		float n;
		float r;
		float o;
		int l;

		Particle() {
			l = 1;
			n = random(1, parent.width / 2);
			r = random(0, TWO_PI);
			o = random(1, random(1, parent.width / n));
		}

		void draw() {
			Color c = parent.getColor().getCurrentColor();
			l++;
			
			parent.pushMatrix();
			parent.rotate(r);
			parent.translate(drawDist(), 0);
			if (shape != null) {
				shape.setColor(c.getRGB(), PApplet.min(l, 255));
				parent.shape(shape.getShape(), 0, 0, parent.width / o / 8, parent.width / o / 8);
			} else {
				parent.fill(c.getRGB(), PApplet.min(l, 255));
				parent.ellipse(0, 0, parent.width / o / 8, parent.width / o / 8);
			}
			parent.popMatrix();

			o -= 0.07;
			o -= oscillator.oscillate(lowSpeedScalar, highSpeedScalar, oscRate, () -> {
				if (frameSkipper.isNewFrame()) {
					skipped++;
					if (skipped % shouldSkip == 0) {
						clockwise = !clockwise;
					}
				}
			});
		}
		

		float drawDist() {
			//Math.atan takes too long.  It can be pre-computed
			return PApplet.atan(n / o) * parent.width / HALF_PI;
		}
	}
}