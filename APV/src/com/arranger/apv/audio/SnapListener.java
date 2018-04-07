package com.arranger.apv.audio;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class SnapListener extends APVPlugin {

	public SnapListener(Main parent) {
		super(parent);
	}

	public boolean isSnap() {
		return parent.getAudio().getBeatInfo().getFreqDetector().isSnare();
	}

}
