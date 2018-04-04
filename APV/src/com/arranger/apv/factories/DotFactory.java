package com.arranger.apv.factories;

import com.arranger.apv.Main;

public class DotFactory extends CircleFactory {

	private static final float LARGE_SHAPE_SIZE = 10;
	private static final float SMALL_SHAPE_SIZE = 2;
	
	public DotFactory(Main parent) {
		super(parent);
	}
	
	public DotFactory(Main parent, float scale) {
		super(parent, scale);
	}

	protected float newShapeSize() {
		float size = parent.random(SMALL_SHAPE_SIZE, LARGE_SHAPE_SIZE);
		return size;
	}
	
}
