package com.arranger.apv;

import java.awt.Color;

public class ColorSystem {

	protected Main parent;
	
	public ColorSystem(Main parent) {
		this.parent = parent;
	}

	public Color getCurrentColor() {
		if (parent.getAudio().getBeatInfo().beat.isKick()) {
			return Color.RED;
		}
		
		return Color.WHITE;
	}
}
