package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.PerlinNoiseWalker;

import processing.core.PApplet;

/**
 * @see https://www.openprocessing.org/sketch/386129
 * 
 * TODO: Move previous mouse
 * TODO: Check line width and alpha
 * TODO: Change num lines / reflection (see {@link #drawLine()}
 */
public class Kaleidoscope extends LiteShapeSystem {

	private static final int STROKE_ALPHA = 50;
	private static final int STROKE_WEIGHT = 3;
	private static final int NUM_LINES_PER_REFLECTION = 4;
	private static final int DEFAULT_NUM_REFLECTIONS = 8;
	private static final float easing = 0.09f;
	
	private PerlinNoiseWalker walker;
	
	int numReflections;
	float easeMouseX;
	float easeMouseY;
	float prevMouseX;
	float prevMouseY;
	
	public Kaleidoscope(Main parent, int numReflections) {
		super(parent);
		this.numReflections = numReflections;
		walker = new PerlinNoiseWalker(parent);
	}

	public Kaleidoscope(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, DEFAULT_NUM_REFLECTIONS));
	}
	
	@Override
	public String getConfig() {
		//{Kaleidoscope : [8]}
		return String.format("{%s : [%d]}", getName(), numReflections);
	}
	
	private static final int WALKER_INC = 1;

	@Override
	public void draw() {
		if (parent.getFrameCount() % WALKER_INC == 0) {
			walker.step(2);
			easeMouseX = prevMouseX = walker.x;
			easeMouseY = prevMouseY = walker.y;
		}
		
		Point2D pt = parent.getCurrentPoint();
		int mouseX = (int) pt.getX();
		int mouseY = (int) pt.getY();

		easeMouseX += (mouseX - easeMouseX) * easing;
		easeMouseY += (mouseY - easeMouseY) * easing;
		for (int i = 0; i < numReflections; i++) {
			parent.pushMatrix();
			parent.translate(parent.width / 2, parent.height / 2);
			parent.rotate(PApplet.radians(i * 360 / numReflections));
			drawLine();
			parent.popMatrix();
		}
		prevMouseX = easeMouseX;
		prevMouseY = easeMouseY;
	}

	void drawLine() {
		Color col = parent.getColor().getCurrentColor();
		parent.stroke(col.getRGB(), STROKE_ALPHA);
		parent.strokeWeight(STROKE_WEIGHT);
		
		for (int i = 0; i <= NUM_LINES_PER_REFLECTION; i++) {
			int width = parent.width;
			int height = parent.height;
			
			if (i % 2 == 0) {
				parent.line(easeMouseX - (width / 2) - i, 
						easeMouseY - (height / 2) - i, 
						prevMouseX - (width / 2) - i,
						prevMouseY - (height / 2) - i);
			} else {
				parent.line(easeMouseX - (width / 2) + i, 
						easeMouseY - (height / 2) + i, 
						prevMouseX - (width / 2) + i,
						prevMouseY - (height / 2) + i);
			}
		}
	}

}
