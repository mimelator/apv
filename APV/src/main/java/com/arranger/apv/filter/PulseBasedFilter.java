package com.arranger.apv.filter;

import com.arranger.apv.Main;

import ddf.minim.analysis.BeatDetect;

public abstract class PulseBasedFilter extends Filter {
	
	protected BeatDetect pulseDetector;

	public PulseBasedFilter(Main parent) {
		super(parent);
		pulseDetector = parent.getAudio().getBeatInfo().getPulseDetector();
	}

}
