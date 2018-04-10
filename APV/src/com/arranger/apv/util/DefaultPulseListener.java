package com.arranger.apv.util;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;

public class DefaultPulseListener extends APVPlugin {
	
	private PulseListener pulseListener;
	private List<PulseHandler> handlers = new ArrayList<PulseHandler>();
	
	@FunctionalInterface
	public static interface PulseHandler {
		public void onPulse();
	}

	public DefaultPulseListener(Main parent) {
		super(parent);
	}
	
	public void registerPulseListener(PulseHandler pulseHandler) {
		if (pulseListener == null) {
			pulseListener = new PulseListener(parent, 1); //lazy Init
		}
		
		handlers.add(pulseHandler);
	}

	public void checkPulse() {
		if (pulseListener.isNewPulse()) {
			firePulse();
		}
	}
	
	private void firePulse() {
		for (PulseHandler h : handlers) {
			h.onPulse();
		}
	}
}
