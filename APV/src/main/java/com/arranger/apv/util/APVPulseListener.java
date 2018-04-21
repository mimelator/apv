package com.arranger.apv.util;

import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;

public class APVPulseListener extends APVCallback {
	
	private PulseListener pulseListener;
	
	public APVPulseListener(Main parent) {
		super(parent, "pulseListeners");
		parent.getSetupEvent().register(() -> {
			pulseListener = new PulseListener(parent, 1);
		});
	}
	
	public void addSettingsMessages() {
		pulseListener.addSettingsMessages();
	}
	
	@Override
	public boolean isEnabled() {
		return super.isEnabled() && pulseListener.isNewPulse();
	}
}