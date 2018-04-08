package com.arranger.apv.bg;

import java.awt.Color;

import com.arranger.apv.Main;

public class OscilatingBackDrop extends BackDropSystem {
	
	private static final int SPEED_LOW = 4;
	private static final int SPEED_HIGH = 12;
	private Color c1, c2;
	private float oscSpeed;

	public OscilatingBackDrop(Main parent, Color c1, Color c2) {
		super(parent);
		this.c1 = c1;
		this.c2 = c2;
		
		oscSpeed = parent.random(SPEED_LOW, SPEED_HIGH);
	}

	@Override
	public void drawBackground() {
		float amt = parent.oscillate(0, 1, oscSpeed);
		int lerpColor = parent.lerpColor(c1.getRGB(), c2.getRGB(), amt);
		parent.background(lerpColor);
		
		String pctString = String.format("%.0f%%", amt * 100);
		parent.addSettingsMessage(" --color from to: " + c1.getRGB() + " -> " + c2.getRGB() + " at: " + pctString);
		parent.addSettingsMessage(" --oscSpeed: " + oscSpeed);
	}
}
