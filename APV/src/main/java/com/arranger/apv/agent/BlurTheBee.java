package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.back.BlurBackDrop;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.systems.lite.LightBee;

public class BlurTheBee extends BaseAgent {

	public BlurTheBee(Main parent) {
		super(parent);
		
		registerAgent(parent.getDrawEvent(), () -> {
			ShapeSystem fgSys = parent.getCurrentScene().getComponentsToDrawScene().fgSys;
			if (fgSys instanceof LightBee) {
				parent.activateNextPlugin(BlurBackDrop.class.getSimpleName(), Main.SYSTEM_NAMES.BACKDROPS, getDisplayName());
			}
		});
		
	}
}
