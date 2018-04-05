package com.arranger.apv.filter;

import com.arranger.apv.Main;

import ddf.minim.analysis.BeatDetect;

public abstract class BeatFilter extends Filter {
	
	protected BeatDetect beatDetect;

	public BeatFilter(Main parent) {
		super(parent);
		beatDetect = parent.getAudio().getBeatInfo().getBeat();
	}

}
