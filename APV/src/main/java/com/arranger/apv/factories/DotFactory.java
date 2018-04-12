package com.arranger.apv.factories;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class DotFactory extends CircleImageFactory {

	private static final float LARGE_SHAPE_SIZE = 10;
	private static final float SMALL_SHAPE_SIZE = 2;
	
	public DotFactory(Main parent) {
		super(parent);
	}
	
	public DotFactory(Main parent, float scale) {
		super(parent, scale);
	}
	
	public DotFactory(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getFloat(0, 1));
	}

	protected float newShapeSize() {
		float size = parent.random(SMALL_SHAPE_SIZE, LARGE_SHAPE_SIZE);
		return size;
	}
	
}
