package com.arranger.apv.systems.lite;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;

import ddf.minim.analysis.FFT;
import processing.core.PApplet;

/**
 * For more information about Minim and additional features, visit
 * http://code.compartmental.net/minim/
 */
public class FreqDetector extends LiteShapeSystem {

	private static final float AMPLITUDE_SCALING_CEILING = 100f;
	private static final float DISPLAY_SCALING_FACTOR = 25;
	private static final int BANDS_TO_SKIP = 0;

	public FreqDetector(Main parent) {
		super(parent);
	}

	@Override
	public void setup() {
		
	}

	@Override
	public void draw() {
		parent.rectMode(CORNERS);

		FFT fft = parent.getAudio().getBeatInfo().getFFT();
		float bounds = fft.avgSize();
		float frequencyWidth = (float)(parent.width / bounds);
		
		Point2D pt = parent.getCurrentPoint();
		float curPosX = (float)pt.getX();
		float curPosY = (float)pt.getY();
		
		Color c = parent.getColor().getCurrentColor();
		
		float minAmp = Float.MAX_VALUE;
		float maxAmp = Float.MIN_VALUE;

		float pctToExpand = (float)BANDS_TO_SKIP / bounds; 
		frequencyWidth *= (1f + pctToExpand);
		
		//I'd like to skip a few of the lowest bands
		for (int index = BANDS_TO_SKIP; index < bounds; index++) {
			int adjustedIndex = index - BANDS_TO_SKIP;
			
			float leftX = adjustedIndex * frequencyWidth;
			float rightX = adjustedIndex * frequencyWidth + frequencyWidth;
			
			//color differently based on the current point supplied by the LocationSystem
			if (curPosX >= leftX && curPosY < rightX) {
				parent.fill(c.getRGB(), 255);
			} else {
				parent.fill(c.getRGB(), 125);
			}

			//Need to scale lower frequencies less than high frequencies
			float pctComplete = (float) adjustedIndex / bounds;
			float scaleFactor = (float) Math.pow(pctComplete, Math.E) * AMPLITUDE_SCALING_CEILING;
			scaleFactor = PApplet.constrain(scaleFactor, 1, AMPLITUDE_SCALING_CEILING);
			float scale = DISPLAY_SCALING_FACTOR * scaleFactor;

			float amplitude = fft.getAvg(index);
			minAmp = Math.min(minAmp, amplitude);
			maxAmp = Math.max(maxAmp, amplitude);
			
			float rectHeight = parent.height - (amplitude * scale);
			parent.rect(leftX, parent.height, rightX, rectHeight);
		}
		
		parent.addSettingsMessage("  --minAmp: " + minAmp);
		parent.addSettingsMessage("  --maxAmp: " + maxAmp);
	}
}
