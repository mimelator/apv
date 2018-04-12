package com.arranger.apv.pl;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.factories.StarFactory;

import processing.core.PShape;

public class StarPL extends APVPlugin {
	
	private static final float SCALE_SMALL = .5f;
	private static final float SCALE_LARGE = 2.5f;
	
	private PShape star;

	public StarPL(Main parent) {
		super(parent);
		
		star = StarFactory.createStar(parent);
		
		parent.getPulseListener().registerPulseListener( ()-> { 
				//location
				Point2D point = parent.getLocationSystem().getCurrentPoint();
				int x = (int)point.getX();
				int y = (int)point.getY();
				
				//color
				Color c = parent.getColorSystem().getCurrentColor();
				star.setFill(c.getRGB());
				
				//scale
				star.scale(parent.random(SCALE_SMALL, SCALE_LARGE));
				
				//move it, paint it and then reset
				parent.translate(x, y);
				parent.shape(star);
				parent.translate(-x, -y);
				star.resetMatrix();
			}  
		);	
	}
}
