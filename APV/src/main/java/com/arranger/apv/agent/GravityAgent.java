package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.systems.lifecycle.GravitySystem;
import com.arranger.apv.util.Configurator;

public class GravityAgent extends PulseAgent {

	public GravityAgent(Main parent, int numPulses) {
		super(parent, numPulses);
	}
	
	public GravityAgent(Configurator.Context ctx) {
		super(ctx);
	}
	
	@Override
	protected void onPulse() {
		ShapeSystem fgSys = parent.getCurrentScene().getComponentsToDrawScene().fgSys;
		if (fgSys instanceof GravitySystem) {
			invokeCommand(Command.GRAVITY);
		}
	}
}
