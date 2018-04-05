package com.arranger.apv.color;

import java.awt.Color;

import com.arranger.apv.Main;

import ddf.minim.analysis.BeatDetect;

/**
 * Basic BeatColor System toggles between red & white
 */
public class BeatColorSystem extends ColorSystem {
	
	public BeatColorSystem(Main parent) {
		super(parent);
	}

	public Color getCurrentColor() {
		BeatDetect beat = parent.getAudio().getBeatInfo().getBeat();
		boolean kick = beat.isKick();
		return kick ? Color.RED : Color.WHITE;
	}
}
