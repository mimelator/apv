package com.arranger.apv.loc;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;

import processing.core.PApplet;

/**
 * http://natureofcode.com/book/introduction/
 */
public class PerlinNoiseWalker extends LocationSystem {

	protected static final int SCALE = 5;
	
	Walker walker = new Walker();
	int lastFrameChecked = -1;
	
	public PerlinNoiseWalker(Main parent) {
		super(parent);
	}

	@Override
	public Point2D getCurrentPoint() {
		while (lastFrameChecked < parent.getFrameCount()) {
			walker.step(SCALE);
			lastFrameChecked++;
		}
		
		return new Point2D.Float(walker.x, walker.y);
	}

	private class Walker {
		float x, y;
		float tx, ty;

		Walker() {
			tx = 0;
			ty = 10000;
		}

		void step(int scale) {
			for (int index = 0; index < scale; index++) {
				x = PApplet.map(parent.noise(tx), 0, 1, 0, parent.width);
				y = PApplet.map(parent.noise(ty), 0, 1, 0, parent.height);
	
				tx += 0.01;
				ty += 0.01;
			}
		}
	}

}
