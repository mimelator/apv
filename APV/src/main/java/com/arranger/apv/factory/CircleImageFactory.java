package com.arranger.apv.factory;

import com.arranger.apv.Main;

/**
 * Now using sprites!
 * 
 * Drawing Ecllipses are way too expensive
 */
public class CircleImageFactory extends SpriteFactory {
	
	public static final String CIRCLE_PNG = "circle.png";
	
	
	public CircleImageFactory(Main parent) {
		super(parent, CIRCLE_PNG);
	}

	public CircleImageFactory(Main parent, float scale) {
		super(parent, CIRCLE_PNG, scale);
	}

	@Override
	public String getConfig() {
		//{CircleImageFactory : []}
		String name = getName();
		return String.format("{%1s : [%2s]}", name, scale);
	}
}
