package com.arranger.apv.pl;

import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;
import com.arranger.apv.helpers.APVCallbackHelper;

public class APVPulseListener extends APVCallbackHelper {
	
	private PulseListener pulseListener;
	
	public APVPulseListener(Main parent) {
		super(parent, Main.SYSTEM_NAMES.PULSELISTENERS);
		parent.getSetupEvent().register(() -> {
			pulseListener = new PulseListener(parent, 1);
		});
	}
	
	public void addSettingsMessages() {
		pulseListener.addSettingsMessages();
	}
	
	public void registerHandler(Handler handler) {
		registerHandler(handler, 1);
	}
	
	public void registerHandler(Handler handler, int numFramesToSkip) {
		registerHandler(parent.getDrawEvent(), handler, numFramesToSkip);
	}
	
	@Override
	public boolean isEnabled() {
		return super.isEnabled() && pulseListener.isNewPulse();
	}
}