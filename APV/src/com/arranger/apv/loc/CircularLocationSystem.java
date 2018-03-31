package com.arranger.apv.loc;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import com.arranger.apv.Main;

public class CircularLocationSystem extends PathLocationSystem {
	
	private static final int LOOP_IN_SECONDS = 5;
	private static final float SCALE = .75f;
	
	public CircularLocationSystem(Main parent) {
		super(parent, LOOP_IN_SECONDS);
	}

	@Override
	protected Shape createPath() {
		float width = parent.width * SCALE;
		float height = parent.height * SCALE;
		float x = parent.width * (1 - SCALE) / 2;
		float y = parent.height * (1 - SCALE) / 2;
		return new Ellipse2D.Float(x, y, width, height);
	}
}