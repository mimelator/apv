package com.arranger.apv.archive;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.systems.lite.LiteShapeSystem;
import com.arranger.apv.util.FFTAnalysis;

import processing.core.PApplet;

/**
 * https://www.openprocessing.org/sketch/385918
 */
public class ShowerSystem extends LiteShapeSystem {

	private static final float AMP_SCALE = 3.5f;

	private static final int OSCILLATION_SCALAR = 2;
	private static final int HIGH_FILL_VAL = 55;
	private static final int LOW_FILL_VAL = 15;

	private static final float STROKE_WEIGHT_LOW = .5f; // 15 was fun
	private static final float STROKE_WEIGHT_HIGH = 5;
	private static int NUM_LINES = 20;
	private static int STROKE_CHANGE_RATE = 150;
	private static int FILL_CHANGE_RATE = 43;

	private int prevStrokeColor;
	private int curStrokeColor;
	private int prevFillColor;
	private int curFillColor;

	private FFTAnalysis fftAnalysis;

	public ShowerSystem(Main parent) {
		super(parent);
		fftAnalysis = new FFTAnalysis(parent);
	}

	@Override
	public void setup() {
		curStrokeColor = prevStrokeColor = parent.getColor().getCurrentColor().getRGB();
		curFillColor = prevFillColor = parent.getColor().getCurrentColor().getRGB();
	}

	@Override
	public void draw() {
		int speed = 3; // Not sure

		int curFillFrame = parent.getFrameCount() % FILL_CHANGE_RATE;
		if (curFillFrame == 0) {
			prevFillColor = curFillColor;
			curFillColor = parent.getColor().getCurrentColor().getRGB();
		}

		int curStrokeFrame = parent.getFrameCount() % STROKE_CHANGE_RATE;
		if (curStrokeFrame == 0) {
			prevStrokeColor = curStrokeColor;
			curStrokeColor = parent.getColor().getCurrentColor().getRGB();
		}

		float n = parent.getFrameCount() * 0.01f;
		float s = parent.width / speed + parent.width / speed * sin(n);
		float r = parent.width / NUM_LINES;

		// oscilate the alpha
		float alpha = parent.oscillate(LOW_FILL_VAL, HIGH_FILL_VAL, OSCILLATION_SCALAR);
		int useFillColor = getColor(curFillFrame, FILL_CHANGE_RATE, prevFillColor, curFillColor);
		parent.fill(useFillColor, alpha);
		parent.addSettingsMessage("  --alpha: " + alpha);
		parent.addSettingsMessage("  --useFillColor: " + useFillColor);

		// change up the stroke
		float strokeWeightLow = STROKE_WEIGHT_LOW * (fftAnalysis.getMaxAmp() * AMP_SCALE);

		float stroke = parent.oscillate(strokeWeightLow, STROKE_WEIGHT_HIGH, OSCILLATION_SCALAR / 3.0f);
		parent.strokeWeight(stroke);

		// interpolate the color
		int useStrokeColor = getColor(curStrokeFrame, STROKE_CHANGE_RATE, prevStrokeColor, curStrokeColor);
		parent.stroke(useStrokeColor);
		parent.beginShape();

		Point2D point = parent.getLocation().getCurrentPoint();
		int mouseX = (int) point.getX();
		int mouseY = (int) point.getY();

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
