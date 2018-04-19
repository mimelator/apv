package com.arranger.apv.systems.lite;

import java.awt.Color;

import com.arranger.apv.Main;

import processing.core.PApplet;

/**
 * https://www.openprocessing.org/sketch/507361
 */
public class PixelAttractor extends LiteShapeSystem {

	private static final int NUM_PIXELS = 5000;//10000;
	float[] c;
	float[] d;

	public PixelAttractor(Main parent) {
		super(parent);
		parent.registerSetupListener(() -> {
			parent.getPulseListener().registerHandler(() -> {
				setup();
			}, 4); //skip every 4 pulses
		});
	}

	@Override
	public void setup() {
		c = new float[3];
		d = new float[3];

		for (int i = 0; i < c.length; i++) {
			d[i] = random(-1, 1);
		}
	}

	float t = 0.5f;

	@Override
	public void draw() {
		t += 0.003;
		parent.fill(0, 5);

		for (int i = 0; i < c.length; i++) {
			c[i] = random(-1, 1);
		}

		//using color, but not location system
		Color col = parent.getColor().getCurrentColor();
		
		for (int i = 0; i < NUM_PIXELS; i++) {

			int x = (int) ((c[0] + 1) * parent.width / 2);  
			int y = (int) ((c[1] + 1) * parent.height / 2); 
			
			parent.set(x, y, parent.color(col.getRed(), col.getGreen(), col.getBlue()));

			for (int j = 0; j < c.length; j++) {
				c[j] = (sin(c[cl(j)] * PI) + d[j] * PApplet.sq(sin(t)))
						* (sin(c[cl(j + 1)] * PI) + d[j] * PApplet.sq(cos(t))) + parent.random(0.002f);
			}
		}
	}

	int cl(int i) {
		return i % c.length;
	}

}
