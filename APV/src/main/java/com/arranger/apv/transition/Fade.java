package com.arranger.apv.transition;

import com.arranger.apv.Main;
import com.arranger.apv.TransitionSystem;
import com.arranger.apv.util.Configurator;

public class Fade extends TransitionSystem {
	

	public Fade(Main parent, int fadesToTransition) {
		super(parent, fadesToTransition);
	}
	
	public Fade(Configurator.Context ctx) {
		super(ctx);
	}

	@Override
	public void doTransition(float pct) {
		doStandardFade(pct);
		parent.image(savedImage.getSavedImage(),  0,  0);
	}
}
