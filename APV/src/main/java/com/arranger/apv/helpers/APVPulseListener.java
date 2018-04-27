package com.arranger.apv.helpers;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;

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
	
	public void registerHandler(Handler handler, APVPlugin pluginToCheck) {
		registerHandler(handler, 1, pluginToCheck);
	}
	
	public void registerHandler(Handler handler, int numFramesToSkip, APVPlugin pluginToCheck) {
		registerHandler(parent.getDrawEvent(), handler, numFramesToSkip, pluginToCheck);
	}
	
	@Override
	public boolean isEnabled() {
		return pulseListener.isNewPulse();
	}
}