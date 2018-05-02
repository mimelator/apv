package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public abstract class LocationAgent extends BaseAgent {

	public LocationAgent(Main parent) {
		super(parent);
		registerAgent(getDrawEvent(), () -> {
			if (shouldChangeLocation()) {
				invokeCommand(Command.CYCLE_LOCATIONS);
			}
		});
	}

	protected abstract boolean shouldChangeLocation();
}
