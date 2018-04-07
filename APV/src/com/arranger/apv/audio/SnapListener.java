package com.arranger.apv.audio;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import ddf.minim.analysis.BeatDetect;

public class SnapListener extends APVPlugin {

	public SnapListener(Main parent) {
		super(parent);
	}

	public boolean isSnap() {
		BeatDetect fd = parent.getAudio().getBeatInfo().getFreqDetector();
		
		int size = fd.detectSize();
		int lower = (int)(size * .6f);
		int upper = (int)(size * .8f);
		int thresh = upper - lower;
		return fd.isRange(lower, upper, thresh);
	}

}
