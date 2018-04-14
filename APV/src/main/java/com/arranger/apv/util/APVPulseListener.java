package com.arranger.apv.util;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;

public class APVPulseListener extends APVPlugin {
	
	private PulseListener pulseListener;
	private List<Listeners> listeners = new ArrayList<Listeners>();
	
	@FunctionalInterface
	public static interface PulseHandler {
		public void onPulse();
	}
	
	public APVPulseListener(Main parent) {
		super(parent);
	}
	
	public void addSettingsMessages() {
		pulseListener.addSettingsMessages();
	}
	
	public void registerPulseListener(PulseHandler pulseHandler) {
		registerPulseListener(pulseHandler, 1);
	}
	
	public void registerPulseListener(PulseHandler pulseHandler, int framesToSkip) {
		if (pulseListener == null) {
			pulseListener = new PulseListener(parent, 1); //lazy Init
		}
		
		listeners.add(new Listeners(pulseHandler, framesToSkip));
	}

	public void checkPulse() {
		if (pulseListener.isNewPulse()) {
			firePulse();
		}
	}
	
	private void firePulse() {
		for (Listeners l : listeners) {
			if (l.shouldHandle()) {
				l.handler.onPulse();
			}
		}
	}
	
	class Listeners {
		private PulseHandler handler;
		private int pulseCount = 0;
		private int pulsesToSkip;
		
		Listeners(PulseHandler handler, int framesToSkip) {
			this.handler = handler;
			this.pulsesToSkip = framesToSkip;
		}
		
		boolean shouldHandle() {
			pulseCount++;
			
			if (pulseCount % pulsesToSkip == 0) {
				return true;
			} else {
				return false;
			}
		}
	}
}
