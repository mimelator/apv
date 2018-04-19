package com.arranger.apv.agent;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.ShapeSystem;
import com.arranger.apv.audio.PulseListener;
import com.arranger.apv.systems.lifecycle.GravitySystem;
import com.arranger.apv.util.Configurator;

public class GravityAgent extends APVPlugin {

	private static final int DEFAULT_PULSES_TO_SKIP = 1;
	private PulseListener pulseListener;
	

	public GravityAgent(Main parent, int numPulses) {
		super(parent);

		parent.registerSetupListener(() -> {
				parent.getAgent().registerHandler(() -> {
					if (pulseListener.isNewPulse()) {
						ShapeSystem fgSys = parent.getCurrentScene().getFgSys();
						if (fgSys instanceof GravitySystem) {
							parent.getCommandSystem().invokeCommand('g');
						}
					}
				});
				
				pulseListener = new PulseListener(parent, numPulses);
		});
	}
	
	public GravityAgent(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, DEFAULT_PULSES_TO_SKIP));
	}
}
