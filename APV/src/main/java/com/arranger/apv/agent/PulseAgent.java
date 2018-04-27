package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;
import com.arranger.apv.util.Configurator;

public abstract class PulseAgent extends BaseAgent {
	
	private static final int DEFAULT_PULSES_TO_SKIP = 1;
	private PulseListener pulseListener;

	public PulseAgent(Main parent, int numPulses) {
		super(parent);
		
		registerAgent(getDrawEvent(), () -> {
			if (pulseListener.isNewPulse()) {
				onPulse();
			}
		});
		
		parent.getSetupEvent().register(() -> {
			pulseListener = new PulseListener(parent, numPulses);
		});
	}

	public PulseAgent(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, DEFAULT_PULSES_TO_SKIP));
	}
	
	@Override
	public String getConfig() {
		if (pulseListener != null) {
			return String.format("{%s : [%d]}", getName(), pulseListener.getPulsesToSkip());
		} else {
			return super.getConfig();
		}
	}
	
	protected abstract void onPulse();
	
}
