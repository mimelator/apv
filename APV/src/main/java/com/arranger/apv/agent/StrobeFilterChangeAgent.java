package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public class StrobeFilterChangeAgent extends BaseAgent {

	public StrobeFilterChangeAgent(Main parent) {
		super(parent);
		
		registerAgent(getStrobeEvent(), () -> {
			invokeCommand(Command.CYCLE_FILTERS);
		});
	}
}
