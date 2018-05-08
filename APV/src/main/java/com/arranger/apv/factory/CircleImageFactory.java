package com.arranger.apv.factory;

import com.arranger.apv.Main;
import com.arranger.apv.util.ImageHelper;

/**
 * Now using sprites!
 * 
 * Drawing Ecllipses are way too expensive
 */
public class CircleImageFactory extends SpriteFactory {
	
	
	public CircleImageFactory(Main parent) {
		this(parent, 1);
	}

	public CircleImageFactory(Main parent, float scale) {
		super(parent, ImageHelper.ICON_NAMES.SIMPLE_CIRCLE.name(), scale);
	}

	@Override
	public String getConfig() {
		//{CircleImageFactory : []}
		return String.format("{%s : [%s]}", getName(), scale);
	}
}
