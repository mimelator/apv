package com.arranger.apv.pl;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.factories.StarFactory;
import com.arranger.apv.util.Configurator;

import processing.core.PShape;

public class StarPL extends APVPlugin {
	
	private static final float SCALE_SMALL = .5f;
	private static final float SCALE_LARGE = 2.5f;
	
	private PShape star;
	private float scaleSmall = SCALE_SMALL, scaleLarge = SCALE_LARGE;

	public StarPL(Main parent) {
		this(parent, SCALE_SMALL, SCALE_LARGE);
	}

	public StarPL(Main parent, float scaleSmall, float scaleLarge) {
		super(parent);
		this.scaleSmall = scaleSmall;
		this.scaleLarge = scaleLarge;
		
		star = StarFactory.createStar(parent);
		registerListener(parent);	
	}
	
	public StarPL(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getFloat(0, SCALE_SMALL), ctx.getFloat(1, SCALE_LARGE));
	}
	
	protected void registerListener(Main parent) {
		parent.getPulseListener().registerPulseListener( ()-> { 
				//location
				Point2D point = parent.getLocation().getCurrentPoint();
				int x = (int)point.getX();
				int y = (int)point.getY();
				
				//color
				Color c = parent.getColor().getCurrentColor();
				star.setFill(c.getRGB());
				
				//scale
				star.scale(parent.random(scaleSmall, scaleLarge));
				
				//move it, paint it and then reset
				parent.translate(x, y);
				parent.shape(star);
				parent.translate(-x, -y);
				star.resetMatrix();
			}  
		);
	}
}
