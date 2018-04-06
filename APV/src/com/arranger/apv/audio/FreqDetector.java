package com.arranger.apv.audio;

import com.arranger.apv.Main;
import com.arranger.apv.systems.lite.LiteShapeSystem;


/**
 * For more information about Minim and additional features, visit
 * http://code.compartmental.net/minim/
 */
public class FreqDetector extends LiteShapeSystem {

//	private static final int GREEN = 200;
//
//	int numberOfOnsetsThreshold = 4;
//	int lowBand = 5;
//	int highBand = 15;
	
	public FreqDetector(Main parent) {
		super(parent);
	}

	@Override
	public void setup() {
		
	}

	@Override
	public void draw() {
		//APVBeatDetector beat = parent.getAudio().getBeatInfo().freqDetector;

		// draw a green rectangle for every detect band
		// that had an onset this frame
/*		int detectSize = beat.detectSize();
		float rectW = parent.width / detectSize;
		for (int i = 0; i < detectSize; ++i) {
			// test one frequency band for an onset
			if (beat.isOnset(i)) {
				parent.fill(0, GREEN, 0);
				parent.rect(i * rectW, 0, rectW, parent.height);
			}
		}

		// draw an orange rectangle over the bands in
		// the range we are querying
		
		// at least this many bands must have an onset for isRange to return true
		if (beat.isRange(lowBand, highBand, numberOfOnsetsThreshold)) {
			parent.fill(232, 179, 2, 200);
			parent.rect(rectW * lowBand, 0, (highBand - lowBand) * rectW, parent.height);
		}
		*/
		
		//beat.drawGraph();
	}

}
