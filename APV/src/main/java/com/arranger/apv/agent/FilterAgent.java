package com.arranger.apv.agent;

import com.arranger.apv.Command;
import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class FilterAgent extends PulseAgent {

	public FilterAgent(Main parent, int numPulses) {
		super(parent, numPulses);
	}
	
	public FilterAgent(Configurator.Context ctx) {
		super(ctx);
	}
	
	@Override
	protected void onPulse() {
		invokeCommand(Command.CYCLE_FILTERS);
	}
}
