package com.arranger.apv.systems.lite;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;

import processing.core.PApplet;

/**
 * https://www.openprocessing.org/sketch/385918
 */
public class ShowerSystem extends LiteShapeSystem {
	
	private static final int OSCILLATION_SCALAR = 4;
	private static final int HIGH_FILL_VAL = 55;
	private static final int LOW_FILL_VAL = 15;
	private static final float STROKE_WEIGHT_LOW = .5f; //15 was fun
	private static final float STROKE_WEIGHT_HIGH = 5;
	private static int NUM_LINES = 20;
	private static int STROKE_CHANGE_RATE = 150;
	private static int FILL_CHANGE_RATE = 43;

	private int prevStrokeColor;
	private int curStrokeColor;
	private int prevFillColor;
	private int curFillColor;
	
	public ShowerSystem(Main parent) {
		super(parent);
	}

	@Override
	public void setup() {
		curStrokeColor = prevStrokeColor = parent.getColorSystem().getCurrentColor().getRGB();
		curFillColor = prevFillColor = parent.getColorSystem().getCurrentColor().getRGB();
	}

	@Override
	public void draw() {
		
		int curFillFrame = parent.frameCount % FILL_CHANGE_RATE;
		if (curFillFrame == 0) {
			prevFillColor = curFillColor;
			curFillColor = parent.getColorSystem().getCurrentColor().getRGB();
		}
		
		int curStrokeFrame = parent.frameCount % STROKE_CHANGE_RATE;
		if (curStrokeFrame == 0) {
			prevStrokeColor = curStrokeColor;
			curStrokeColor = parent.getColorSystem().getCurrentColor().getRGB();
		}
		
		float n = parent.frameCount * 0.01f;
		float s = parent.width / 3 + parent.width / 3 * sin(n);
		float r = parent.width / NUM_LINES;
		
		parent.blendMode(BLEND);
		
		//oscilate the alpha
		float alpha = parent.oscillate(LOW_FILL_VAL, HIGH_FILL_VAL, OSCILLATION_SCALAR);
		int useFillColor = getColor(curFillFrame, FILL_CHANGE_RATE, prevFillColor, curFillColor); 
		parent.fill(useFillColor, alpha);
		parent.addDebugMsg("alpha: " + alpha);
		parent.addDebugMsg("useFillColor: " + useFillColor);
		
		//change up the stroke
		float stroke = parent.oscillate(STROKE_WEIGHT_LOW, STROKE_WEIGHT_HIGH, OSCILLATION_SCALAR / 3.0f);
		parent.strokeWeight(stroke);
		
		//interpolate the color
		int useStrokeColor = getColor(curStrokeFrame, STROKE_CHANGE_RATE, prevStrokeColor, curStrokeColor);
		parent.stroke(useStrokeColor);
		parent.beginShape();
		
		Point2D point = parent.getLocationSystem().getCurrentPoint();
		int mouseX = (int)point.getX();
		int mouseY = (int)point.getY();
		
		for (int i = 0; i < NUM_LINES; i++) {
			
			float mx = mouseX + cos(i * 0.1f + n) * s;
			float my = mouseY + sin(i * 0.1f + n) * s;

			parent.curveVertex(mx, -s * 0.5f + s + i * r);
			parent.curveVertex(mx, my);
			parent.curveVertex(my, mx);
			parent.curveVertex(-s * 0.5f + s + i * r, my);
		}
		parent.endShape();
	}

	protected int getColor(int currentFrame, int numFrames, int color1, int color2) {
		float amt = PApplet.map(currentFrame, 0, numFrames, 0, 1);
		int useColor = parent.lerpColor(color1, color2, amt);
		return useColor;
	}
}
