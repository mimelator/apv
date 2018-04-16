package com.arranger.apv.systems.lite;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.color.ColorSystem;
import com.arranger.apv.util.FFTAnalysis;

import processing.core.PApplet;

/*
 * twisted lines
 *
 * @see https://www.openprocessing.org/sketch/402961
 */
public class TwistedLines extends LiteShapeSystem {

	private static final int LINE_WIDTH = 5;
	private static final int NUM_LINES = 3;
	private static final int LOUD_AMP = 3;
	private static final int QUIET_AMP = 0;
	private static final int SMALL_HEIGHT = 25;
	private static final int LARGE_HEIGHT = 200;
	
	protected FFTAnalysis fftAnalysis;
	Color [] colors = new Color[NUM_LINES];

	public TwistedLines(Main parent) {
		super(parent);
		fftAnalysis = new FFTAnalysis(parent);
	}

	@Override
	public void setup() {
	}

	@Override
	public void draw() {
		//Dynamic amplitude based on audio
		int heightScalar = (int)fftAnalysis.getMappedAmpInv(QUIET_AMP, LOUD_AMP, SMALL_HEIGHT, LARGE_HEIGHT);
		
		//get array of colors  Just three for now
		ColorSystem c = parent.getColor();
		for (int index = 0; index < colors.length; index++) {
			colors[index] = c.getCurrentColor();
		};
		
		int frameCount = parent.getFrameCount();
		parent.noFill();
		parent.strokeWeight(LINE_WIDTH);
		for (int i = 0; i < NUM_LINES; i++) {
			Color useCol = colors[i];
			parent.stroke(useCol.getRed(), useCol.getGreen(), useCol.getBlue());
			parent.beginShape();
			for (int w = -20; w < parent.width + 20; w += 5) {
				int h = parent.height / 2;
				h += heightScalar * PApplet.sin(w * 0.03f + frameCount * 0.07f + i * TWO_PI / 3)
						* PApplet.pow(PApplet.abs(PApplet.sin(w * 0.001f + frameCount * 0.02f)), 5);
				parent.curveVertex(w, h);
			}
			parent.endShape();
		}
	}
}
