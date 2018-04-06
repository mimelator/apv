package com.arranger.apv.filter;

import com.arranger.apv.Main;
import com.arranger.apv.audio.APVBeatDetector;

public abstract class PulseBeatFilter extends Filter {
	
	protected APVBeatDetector pulseDetector;

	public PulseBeatFilter(Main parent) {
		super(parent);
		pulseDetector = parent.getAudio().getBeatInfo().getPulseDetector();
	}

}
