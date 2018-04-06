package com.arranger.apv.color;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.audio.APVBeatDetector;

/**
 * Basic BeatColor System toggles between red & white
 */
public class BeatColorSystem extends ColorSystem {
	
	public BeatColorSystem(Main parent) {
		super(parent);
	}

	public Color getCurrentColor() {
		APVBeatDetector beat = parent.getAudio().getBeatInfo().getPulseDetector();
		boolean pulse = beat.isOnset();
		return pulse ? Color.RED : Color.WHITE;
	}
}
