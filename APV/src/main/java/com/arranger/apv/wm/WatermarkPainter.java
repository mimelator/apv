package com.arranger.apv.wm;

import java.awt.geom.Point2D;

import com.arranger.apv.APV;
import com.arranger.apv.Main;
import com.arranger.apv.shader.DynamicWatermark;
import com.arranger.apv.shader.Shader;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.SafePainter.LOCATION;

import processing.core.PGraphics;
import processing.core.PImage;

public class WatermarkPainter extends ShapeSystem {
	
	private static final int DEFAULT_NUM_FRAMES = 500;
	private static final int TEXT_SIZE = 250;
	
	private int numFrames;
	private float scale;
	private String msg;
	private SafePainter.LOCATION location;
	private DynamicWatermark dw;
	
	
	public WatermarkPainter(Main parent, int numFrames, String msg, float scale, LOCATION location) {
		super(parent, null);
		this.numFrames = numFrames;
		this.scale = scale;
		this.msg = msg;
		this.location = location;
	}

	public WatermarkPainter(Configurator.Context ctx) {
		this(ctx.getParent(), 
				ctx.getInt(0, DEFAULT_NUM_FRAMES),
				ctx.getString(1, ""),
				ctx.getFloat(2, 1),
				LOCATION.valueOf(ctx.getString(3, LOCATION.MIDDLE.name())));
	}
	
	@Override
	public String getConfig() {
		// {WatermarkPainter : [${apv.watermarkFrames}, "Wavelength Rocks", .5, LOWER_RIGHT]}
		return String.format("{%s : [%s, \"%s\", %s, %s]}", getName(),
							numFrames,
							msg,
							scale,
							location.name());
	}

	@Override
	public void draw() {
		if (dw == null) {
			dw = new DynamicWatermark(parent, .5f, msg, generateImage(), null);
		}
		APV<Shader> shaders = parent.getShaders();
		shaders.setEnabled(true);
		shaders.setNextPlugin(dw, "WatermarkPainter", false);
	}
	
	protected PImage generateImage() {
		Point2D coordinatesForLocation = new SafePainter(parent, null).getCoordinatesForLocation(location);
		PGraphics g = parent.createGraphics(parent.width, parent.height);
		g.beginDraw();
		g.stroke(255); //Don't use color as this is a mask situation
		g.textSize(TEXT_SIZE * scale);
		g.textAlign(location.getAlignX(), location.getAlignY());
		g.text(msg, (float)coordinatesForLocation.getX(), (float)coordinatesForLocation.getY());
		g.endDraw();
		g.dispose();
		
		return g.get();
	}

	@Override
	public void onFactoryUpdate() {
		
	}
	
	public int getNumFrames() {
		return numFrames;
	}
}