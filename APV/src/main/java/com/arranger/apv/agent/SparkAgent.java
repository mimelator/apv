package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.ShapeSystem;
import com.arranger.apv.systems.lite.SpraySpark;
import com.arranger.apv.util.Configurator;

public class SparkAgent extends PulseAgent {

	public SparkAgent(Main parent, int numPulses) {
		super(parent, numPulses);
	}
	
	public SparkAgent(Configurator.Context ctx) {
		super(ctx);
	}

	@Override
	protected void onPulse() {
		ShapeSystem bgSys = parent.getCurrentScene().getComponentsToDrawScene().bgSys;
		if (!(bgSys instanceof SpraySpark)) {
			parent.getSparkEvent().fire();
		}
	}
}
