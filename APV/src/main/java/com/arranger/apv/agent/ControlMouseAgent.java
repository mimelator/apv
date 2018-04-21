package com.arranger.apv.agent;

import com.arranger.apv.APV;
import com.arranger.apv.ControlSystem;
import com.arranger.apv.Main;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.loc.MouseLocationSystem;

import processing.core.PConstants;

public class ControlMouseAgent extends BaseAgent {

	public ControlMouseAgent(Main parent) {
		super(parent);

		registerAgent(getDrawEvent(), () -> {
			if (shouldChangeLocation()) {
				invokeCommand(PConstants.ENTER);
			}
		});
	}
	
	protected boolean shouldChangeLocation() {
		APV<LocationSystem> locations = parent.getLocations();
		ControlSystem cs = parent.getControl();
		LocationSystem	ls = locations.getPlugin();
		if (!cs.allowsMouseLocation() && ls instanceof MouseLocationSystem) {
			return true;
		} else {
			return false;
		}
	}

}
