package com.arranger.apv.shader;

import java.awt.geom.Point2D;
import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.util.draw.SafePainter;

import processing.core.PGraphics;

public class DynamicWatermark extends Watermark {
	
	public DynamicWatermark(Main parent, float alpha, float textSize, String text, SafePainter.LOCATION location, List<SHADERS> shaders) {
		super(parent, text, alpha, false, null, shaders);
		
		Point2D coordinatesForLocation = new SafePainter(parent, null).getCoordinatesForLocation(location);
		PGraphics g = parent.createGraphics(parent.width, parent.height);
		g.beginDraw();
		g.stroke(255); //Don't use color as this is a mask situation
		g.textSize(textSize);
		g.textAlign(location.getAlignX(), location.getAlignY());
		g.text(text, (float)coordinatesForLocation.getX(), (float)coordinatesForLocation.getY());
		g.endDraw();
		
		this.image = g.get();
	}
}
