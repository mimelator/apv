package com.arranger.apv.transition;

import com.arranger.apv.Main;
import com.arranger.apv.TransitionSystem;

public class Fade extends TransitionSystem {
	

	public Fade(Main parent, int fadesToTransition) {
		super(parent, fadesToTransition);
	}

	@Override
	public void doTransition(float pct) {
		doStandardFade(pct);
		parent.image(savedImage,  0,  0);
	}
}
