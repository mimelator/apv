package com.arranger.apv;

import java.awt.Color;

import ddf.minim.analysis.BeatDetect;

/**
 * TODO lots of work here
 */
public class ColorSystem {

	protected Main parent;
	
	public ColorSystem(Main parent) {
		this.parent = parent;
	}

	public Color getCurrentColor() {
		BeatDetect beat = parent.getAudio().getBeatInfo().getBeat();
		boolean kick = beat.isKick();
		return kick ? Color.RED : Color.WHITE;
	}
}
