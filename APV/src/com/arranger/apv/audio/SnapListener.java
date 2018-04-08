package com.arranger.apv.audio;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.MultiFrameSkipper;

import ddf.minim.analysis.BeatDetect;

public class SnapListener extends APVPlugin {

	protected MultiFrameSkipper frameSkipper;
	
	public SnapListener(Main parent, int framesToSkip) {
		super(parent);
		frameSkipper = new MultiFrameSkipper(parent, framesToSkip);
	}

	public boolean isSnap() {
		if (!frameSkipper.isNewFrame()) {
			return false;
		}
		
		BeatDetect fd = parent.getAudio().getBeatInfo().getFreqDetector();
		
		int size = fd.detectSize();
		int lower = (int)(size * .6f);
		int upper = (int)(size * .8f);
		int thresh = upper - lower;
		return fd.isRange(lower, upper, thresh);
	}

}
