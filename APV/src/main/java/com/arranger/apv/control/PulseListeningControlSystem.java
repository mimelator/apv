package com.arranger.apv.control;

import com.arranger.apv.CommandSystem;
import com.arranger.apv.ControlSystem;
import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;

import processing.event.KeyEvent;

public abstract class PulseListeningControlSystem extends ControlSystem {
	
	protected PulseListener autoSkipPulseListener;

	public PulseListeningControlSystem(Main parent) {
		super(parent);
		
		autoSkipPulseListener = new PulseListener(parent, getDefaultPulsesToSkip());
		
		parent.getSetupEvent().register(() -> {
			CommandSystem cs = parent.getCommandSystem();
			cs.registerCommand(']', "Pulse++", "Increases the number of pulses to skip in auto/perlin mode",
					event -> autoSkipPulseListener.incrementPulsesToSkip());
			cs.registerCommand('[', "Pulse--", "Deccreases the number of pulses to skip in auto/perlin mode",
					event -> autoSkipPulseListener.deccrementPulsesToSkip());
		});
	}

	protected abstract int getDefaultPulsesToSkip();
	protected abstract KeyEvent _getNextCommand();

	@Override
	public boolean allowsMouseLocation() {
		return false;
	}
	
	@Override
	public void addSettingsMessages() {
		super.addSettingsMessages();
		autoSkipPulseListener.addSettingsMessages();
	}
	
	@Override
	public KeyEvent getNextCommand() {
		if (autoSkipPulseListener.isNewPulse()) {
			return _getNextCommand();
		} else {
			return null;
		}
	}
}
