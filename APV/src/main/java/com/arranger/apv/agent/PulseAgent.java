package com.arranger.apv.agent;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;
import com.arranger.apv.util.Configurator;

public abstract class PulseAgent extends APVPlugin {
	
	private static final int DEFAULT_PULSES_TO_SKIP = 1;
	private PulseListener pulseListener;

	public PulseAgent(Main parent, int numPulses) {
		super(parent);
		parent.registerSetupListener(() -> {
			parent.getAgent().registerHandler(() -> {
				if (pulseListener.isNewPulse()) {
					onPulse();
				}
			});
			
			pulseListener = new PulseListener(parent, numPulses);
		});
	}

	public PulseAgent(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, DEFAULT_PULSES_TO_SKIP));
	}
	
	protected abstract void onPulse();
	
	protected void invokeCommand(char c) {
		parent.getCommandSystem().invokeCommand(c);
	}
}
