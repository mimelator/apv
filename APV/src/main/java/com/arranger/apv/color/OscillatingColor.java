package com.arranger.apv.color;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.gradient.GradientHelper;
import com.arranger.apv.util.Configurator;

public class OscillatingColor extends BeatColorSystem {

	private static final int DEFAULT_SCALAR = 5;
	private float oscScalar;
	private GradientHelper gradientHelper;
	
	public OscillatingColor(Main parent) {
		this(parent, DEFAULT_SCALAR, null);
	}
	
	public OscillatingColor(Main parent, float oscScalar) {
		this(parent, oscScalar, null);
	}
	
	public OscillatingColor(Main parent, float oscScalar, GradientHelper gradientHelper) {
		super(parent);
		this.oscScalar = oscScalar;
		this.gradientHelper = gradientHelper;
		
		LinearGradientPaint lgp = (gradientHelper == null) ? generateSampleLG() : gradientHelper.getLinearGradientPaint();
		parent.getColorHelper().registerGradientListener(getDisplayName(), lgp, gp -> {
			this.gradientHelper = new GradientHelper(parent, gp);
		});
	}
	
	public OscillatingColor(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getFloat(0, DEFAULT_SCALAR), (GradientHelper)ctx.loadPlugin(1));
	}
	
	public Color getCurrentColor() {
		float pct = parent.oscillate(0, 1, oscScalar);
		if (gradientHelper != null) {
			return gradientHelper.getColor(pct);
		} else {
			return Color.getHSBColor(pct, 1, 1);
		}
	}
	
	@Override
	public String getConfig() {
		if (gradientHelper != null) {
			return String.format("{%s : [%s, %s]}", getName(), oscScalar, gradientHelper.getConfig());
		} else {
			return String.format("{%s : [%s]}", getName(), oscScalar);
		}
	}

	@Override
	protected boolean listenForColorChanges() {
		return false;
	}
	
	protected LinearGradientPaint generateSampleLG() {
		Point2D start = new Point2D.Float(0, 0);
		Point2D end = new Point2D.Float(50, 50);
		float[] dist = { 0.0f, 0.2f, 1.0f };
		Color[] colors = { Color.RED, Color.WHITE, Color.BLUE };
		return new LinearGradientPaint(start, end, dist, colors);
	}
}