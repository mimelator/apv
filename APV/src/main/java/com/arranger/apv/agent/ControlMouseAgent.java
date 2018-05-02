package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.control.ControlSystem;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.loc.MouseLocationSystem;

public class ControlMouseAgent extends LocationAgent {

	public ControlMouseAgent(Main parent) {
		super(parent);
	}
	
	protected boolean shouldChangeLocation() {
		ControlSystem cs = parent.getControl();
		LocationSystem	ls = parent.getLocations().getPlugin();
		return !cs.allowsMouseLocation() && ls instanceof MouseLocationSystem;
	}
}
