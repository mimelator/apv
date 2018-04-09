package com.arranger.apv.transition;

import com.arranger.apv.Main;
import com.arranger.apv.TransitionSystem;

public class Swipe extends TransitionSystem {

	public Swipe(Main parent, int fadesToTransition) {
		super(parent, fadesToTransition);
	}

	@Override
	public void doTransition(float pct) {
		pct = 1 - pct;
		parent.image(savedImage, pct * parent.width, pct * parent.height);
	}

}
