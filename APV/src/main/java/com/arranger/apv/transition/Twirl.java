package com.arranger.apv.transition;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class Twirl extends TransitionSystem {

	public Twirl(Main parent, int fadesToTransition) {
		super(parent, fadesToTransition);
	}
	
	public Twirl(Configurator.Context ctx) {
		super(ctx);
	}

	@Override
	public void doTransition(float pct) {
		doStandardFade(pct);

		//rotate around the center
		parent.imageMode(CENTER);
		parent.translate(parent.width / 2, parent.height / 2);
		parent.rotate(parent.oscillate(0, TWO_PI, 3));
		parent.image(savedImage.getSavedImage(),  0,  0);
	}
}
