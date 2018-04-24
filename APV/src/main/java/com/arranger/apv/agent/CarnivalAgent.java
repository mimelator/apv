package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.systems.lite.cycle.CarnivalShapeSystem;
import com.arranger.apv.util.Configurator;

public class CarnivalAgent extends PulseAgent {

	public CarnivalAgent(Main parent, int numPulses) {
		super(parent, numPulses);
	}
	
	public CarnivalAgent(Configurator.Context ctx) {
		super(ctx);
	}

	@Override
	protected void onPulse() {
		ShapeSystem fgSys = parent.getCurrentScene().getComponentsToDrawScene().fgSys;
		if (!(fgSys instanceof CarnivalShapeSystem)) {
			parent.getCarnivalEvent().fire();
		}
	}
}