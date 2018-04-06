package com.arranger.apv.filter;

import com.arranger.apv.Main;

import ddf.minim.analysis.BeatDetect;

public abstract class PulseBeatFilter extends Filter {
	
	protected BeatDetect pulseDetector;

	public PulseBeatFilter(Main parent) {
		super(parent);
		pulseDetector = parent.getAudio().getBeatInfo().getPulseDetector();
	}

}
