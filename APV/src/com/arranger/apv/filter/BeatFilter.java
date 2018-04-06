package com.arranger.apv.filter;

import com.arranger.apv.Main;
import com.arranger.apv.audio.APVBeatDetector;

public abstract class BeatFilter extends Filter {
	
	protected APVBeatDetector beatDetect;

	public BeatFilter(Main parent) {
		super(parent);
		beatDetect = parent.getAudio().getBeatInfo().getBeat();
	}

}
