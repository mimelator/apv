package com.arranger.apv.transition;

import com.arranger.apv.Main;
import com.arranger.apv.TransitionSystem;
import com.arranger.apv.util.Configurator;

public class Swipe extends TransitionSystem {

	public Swipe(Main parent, int fadesToTransition) {
		super(parent, fadesToTransition);
	}
	
	public Swipe(Configurator.Context ctx) {
		super(ctx);
	}

	@Override
	public void doTransition(float pct) {
		doStandardFade(pct);
		
		pct = 1 - pct;
		parent.image(savedImage, pct * parent.width, pct * parent.height);
	}

}
