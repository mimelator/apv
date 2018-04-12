package com.arranger.apv.color;

import java.awt.Color;

import com.arranger.apv.Main;

/**
 * Basic BeatColor System toggles between red & white
 */
public class BeatColorSystem extends ColorSystem {
	
	public BeatColorSystem(Main parent) {
		super(parent);
	}

	public Color getCurrentColor() {
		boolean pulse =  parent.getAudio().getBeatInfo().getPulseDetector().isOnset();
		return pulse ? Color.RED : Color.WHITE;
	}
}
