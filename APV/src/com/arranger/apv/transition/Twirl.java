package com.arranger.apv.transition;

import com.arranger.apv.Main;
import com.arranger.apv.TransitionSystem;

public class Twirl extends TransitionSystem {

	public Twirl(Main parent, int fadesToTransition) {
		super(parent, fadesToTransition);
	}

	@Override
	public void doTransition(float pct) {
		doStandardFade(pct);

		//rotate around the center
		parent.imageMode(CENTER);
		parent.translate(parent.width / 2, parent.height / 2);
		parent.rotate(parent.oscillate(0, TWO_PI, 3));
		parent.image(savedImage,  0,  0);
	}
}
