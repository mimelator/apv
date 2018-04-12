package com.arranger.apv.transition;

import com.arranger.apv.Main;
import com.arranger.apv.TransitionSystem;
import com.arranger.apv.util.Configurator;

public class Shrink extends TransitionSystem {

	public Shrink(Main parent, int fadesToTransition) {
		super(parent, fadesToTransition);
	}
	
	public Shrink(Configurator.Context ctx) {
		super(ctx);
	}

	@Override
	public void doTransition(float pct) {
		doStandardFade(pct);
		
		int targetDimensionX = (int)(pct * parent.width);
		int targteDimensionY = (int)(pct * parent.height);
		
		int transX = (parent.width / 2) - (targetDimensionX / 2);
		int transY = (parent.height / 2) - (targteDimensionY / 2);
		parent.image(savedImage, transX, transY, targetDimensionX, targteDimensionY);
		
	}

}
