package com.arranger.apv.transition;

import com.arranger.apv.Main;
import com.arranger.apv.TransitionSystem;

import processing.core.PApplet;

public class Fade extends TransitionSystem {
	
	private static final int INITIAL_ALPHA_FOR_FADE = 90;

	public Fade(Main parent) {
		super(parent);
	}

	@Override
	public void doTransition(float pct) {
		float alpha = PApplet.lerp(0, INITIAL_ALPHA_FOR_FADE, pct);
		parent.tint(255, alpha);  // Display at half opacity
		parent.image(savedImage,  0,  0);
		
	}
}
