package com.arranger.apv.audio;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.systems.lite.LiteShapeSystem;

import ddf.minim.AudioSource;
import ddf.minim.analysis.FFT;

/**
 * For more information about Minim and additional features, visit
 * http://code.compartmental.net/minim/
 */
public class FreqDetector extends LiteShapeSystem {

	FFT fftLog;
	float spectrumScale = 50;
	AudioSource source;

	public FreqDetector(Main parent) {
		super(parent);
	}

	@Override
	public void setup() {
		source = parent.getAudio().getBeatInfo().getSource();
		fftLog = new FFT(source.bufferSize(), source.sampleRate());
		fftLog.logAverages(15, 5); // This is a 'tuned' set of buckets that i like
	}

	@Override
	public void draw() {
		parent.rectMode(CORNERS);
		parent.textSize(18);

		fftLog.forward(source.mix);
		int w = parent.width / fftLog.avgSize();
		float bounds = fftLog.avgSize();
		
		Point2D pt = parent.getLocationSystem().getCurrentPoint();
		float x = (float)pt.getX();
		float y = (float)pt.getY();
		
		Color c = parent.getColorSystem().getCurrentColor();

		for (int i = 0; i < bounds; i++) {
			
			float leftX = i * w;
			float rightX = i * w + w;
			
			if (x >= leftX && y < rightX) {
				parent.fill(c.getRGB(), 255);
			} else {
				parent.fill(c.getRGB(), 125);
			}

			// Adjust the scale according to a scale
			// Range of values is 0 to 100

			float f = (float) i;
			float pctComplete = f / bounds;
			float scaleFactor = (float) Math.pow(pctComplete, Math.E) * 100;
			if (scaleFactor < 1)
				scaleFactor = 1;
			float scale = spectrumScale * scaleFactor;

			float h = parent.height - fftLog.getAvg(i) * scale;
			parent.rect(leftX, parent.height, rightX, h);
		}
	}
}
