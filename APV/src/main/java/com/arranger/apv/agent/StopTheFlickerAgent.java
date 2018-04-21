package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.ShapeSystem;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.loc.PathLocationSystem;
import com.arranger.apv.systems.lite.GridShapeSystem;

import processing.core.PConstants;

public class StopTheFlickerAgent extends BaseAgent {

	public StopTheFlickerAgent(Main parent) {
		super(parent);
		
		registerAgent(getDrawEvent(), () -> {
			LocationSystem ls = parent.getLocations().getPlugin();
			if (ls instanceof PathLocationSystem) {
				ShapeSystem ss = parent.getBackgrounds().getPlugin();
				if (ss instanceof GridShapeSystem) {
					if (((PathLocationSystem)ls).isSplitter()) {
						invokeCommand(PConstants.ENTER);
					}
				}
			}
		});
	}
}