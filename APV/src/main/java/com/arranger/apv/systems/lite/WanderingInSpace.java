package com.arranger.apv.systems.lite;

import java.awt.Color;

import com.arranger.apv.Main;
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
	
	Particle[] p = new Particle[800];
	int diagonal;
	float rotation = 0;
	
	int oscRate;
	float highSpeedScalar, lowSpeedScalar;
	
	//Dont reverse every time
	int shouldSkip = 2; //Might eventually parameterize
	int skipped = 0;

	public WanderingInSpace(Main parent, float lowSpeedScalar, float highSpeedScalar, int oscRate) {
		super(parent);
		oscillator = new Oscillator(parent);
		frameSkipper = new SingleFrameSkipper(parent);
		this.oscRate = oscRate;
		this.highSpeedScalar = highSpeedScalar;
		this.lowSpeedScalar = lowSpeedScalar;
	}
	
	public WanderingInSpace(Configurator.Context ctx) {
		this(ctx.getParent(), 
				ctx.getFloat(0, LOW_SPEED_SCALAR),
				ctx.getFloat(1, HIGH_SPEED_SCALAR),
				ctx.getInt(2, OSC_RATE));
	}
	
	@Override
	public String getConfig() {
		//{WanderingInSpace : [.01f, .06f, 20]}
		return String.format("{%s : [%s, %s, %d]}", getName(), lowSpeedScalar, highSpeedScalar, oscRate);
	}

	@Override
	public void setup() {
		for (int i = 0; i < p.length; i++) {
			p[i] = new Particle();
			p[i].o = random(1, random(1, parent.width / p[i].n));
		}

		diagonal = (int) PApplet.sqrt(parent.width * parent.width + parent.height * parent.height) / 2;
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
			parent.fill(c.getRGB(), PApplet.min(l, 255));
			parent.ellipse(0, 0, parent.width / o / 8, parent.width / o / 8);
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