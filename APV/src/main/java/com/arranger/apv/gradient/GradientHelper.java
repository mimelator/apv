package com.arranger.apv.gradient;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.Raster;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

import processing.core.PApplet;

public class GradientHelper extends APVPlugin {
	
	private GradientHelperPoints points;
	private GradientHelperColors colors;
	private GradientHelperFractions fractions;
	
	private LinearGradientPaint paint;
	private int width;
	private Raster raster;

	public GradientHelper(Main parent, LinearGradientPaint paint) {
		super(parent);
		this.paint = paint;
		createRaster(paint);
	}

	public GradientHelper(Main parent, GradientHelperPoints points, GradientHelperColors colors, GradientHelperFractions fractions) {
		super(parent);
		paint = new LinearGradientPaint(points.getStartPoint(), 
				points.getEndPoint(), fractions.getFractions(), colors.getColors());
		createRaster(paint);
	}
	
	public GradientHelper(Configurator.Context ctx) {
		this(ctx.getParent(), 
				(GradientHelperPoints)ctx.loadPlugin(0), 
				(GradientHelperColors)ctx.loadPlugin(1), 
				(GradientHelperFractions)ctx.loadPlugin(2));
	}
	
	public Color getColor(float pct) {
		int index = (int) (pct * width);
		index = PApplet.constrain(index, 0, width - 1);

		int[] pixel = raster.getPixel(index, 0, new int[3]);
		Color result = new Color(pixel[0], pixel[1], pixel[2]);
		return result;
	}
	
	public LinearGradientPaint getLinearGradientPaint() {
		return paint;
	}

	@Override
	public String getConfig() {
		//	{GradientHelper : [
		//						{GradientHelperPoints : PULSE, 5}	
		//	            		{GradientHelperColors : [BLACK, "(128, 0, 128)", RED]}
		//	            		{GradientHelperFractions : [0, .1, 1]}
		//	            	]}
		
		if (points == null || colors == null || fractions == null) {
			generateHelperObjects();
		}
		
		return String.format("{%s : [%s, %s, %s]}", getName(),
				points.getConfig(),
				colors.getConfig(),
				fractions.getConfig());
	}
	
	protected void createRaster(LinearGradientPaint paint) {
		width = (int) paint.getEndPoint().getX();
		Rectangle bounds = new Rectangle(0, 0, width, 1);
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		PaintContext context = paint.createContext(null, bounds, bounds, new AffineTransform(), hints);
		raster = context.getRaster(0, 0, width, 1);
	}
	
	protected void generateHelperObjects() {
		points = new GradientHelperPoints(parent, paint.getStartPoint(), paint.getEndPoint());
		fractions = new GradientHelperFractions(parent, paint.getFractions());
		colors = new GradientHelperColors(parent, paint.getColors());
	}
}
