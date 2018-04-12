package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import processing.core.PApplet;

public class PerlinNoiseWalker extends APVPlugin {
	public float x, y;
	protected float tx, ty;

	public PerlinNoiseWalker(Main parent) {
		super(parent);
		tx = 0;
		ty = 10000;
	}

	public void step(int scale) {
		for (int index = 0; index < scale; index++) {
			x = PApplet.map(parent.noise(tx), 0, 1, 0, parent.width);
			y = PApplet.map(parent.noise(ty), 0, 1, 0, parent.height);

			tx += 0.01;
			ty += 0.01;
		}
	}
}