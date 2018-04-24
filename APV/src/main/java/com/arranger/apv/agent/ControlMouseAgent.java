package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.control.ControlSystem;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.loc.MouseLocationSystem;

public class ControlMouseAgent extends BaseAgent {

	public ControlMouseAgent(Main parent) {
		super(parent);

		registerAgent(getDrawEvent(), () -> {
			if (shouldChangeLocation()) {
				invokeCommand(Command.CYCLE_LOCATIONS);
			}
		});
	}
	
	protected boolean shouldChangeLocation() {
		ControlSystem cs = parent.getControl();
		LocationSystem	ls = parent.getLocations().getPlugin();
		return !cs.allowsMouseLocation() && ls instanceof MouseLocationSystem;
	}
}
