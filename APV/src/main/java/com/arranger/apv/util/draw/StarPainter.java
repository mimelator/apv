package com.arranger.apv.util.draw;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import processing.core.PShape;

public class StarPainter extends APVPlugin {
	
	private static final float SCALE_SMALL = .5f;
	private static final float SCALE_LARGE = 2.5f;
	
	private static final int ALPHA_LOW = 50;
	private static final int ALPHA_HIGH = 200;
	
	private PShape star;
	private float scaleSmall = SCALE_SMALL, scaleLarge = SCALE_LARGE;

	public StarPainter(Main parent) {
		super(parent);
		star = new StarMaker(parent).createStar();
		
		parent.getStarEvent().register(() -> {
			drawStar();
		});
	}
	
	public void drawStar() {
		new SafePainter(parent, () ->  {
			//location
			Point2D point = parent.getCurrentPoint();
			int x = (int)point.getX();
			int y = (int)point.getY();
			
			//color w/transparency
			Color c = parent.getColor().getCurrentColor();
			Color t = new Color(c.getRed(), 
								c.getGreen(), 
								c.getBlue(), 
								(int)parent.random(ALPHA_LOW, ALPHA_HIGH));
			star.setFill(t.getRGB());
			
			//scale
			star.scale(parent.random(scaleSmall, scaleLarge));
			
			//move it, paint it and then reset
			parent.translate(x, y);
			parent.shape(star);
			parent.translate(-x, -y);
			star.resetMatrix();
			
		}).paint();
	}
}
