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
		
		CommandSystem cs = parent.getCommandSystem();
		cs.registerCommand(']', "Pulse++", "Increases the number of pulses to skip in auto/perlin mode", event -> autoSkipPulseListener.incrementPulsesToSkip());
		cs.registerCommand('[', "Pulse--", "Deccreases the number of pulses to skip in auto/perlin mode", event -> autoSkipPulseListener.deccrementPulsesToSkip());
	}

	protected abstract int getDefaultPulsesToSkip();
	protected abstract KeyEvent _getNextCommand();

	@Override
	public boolean allowsMouseLocation() {
		return false;
	}
	
	@Override
	public void addSettingsMessages() {
		_init();
		
		super.addSettingsMessages();
		parent.addSettingsMessage("   ---Pulses to Skip: " + autoSkipPulseListener.getPulsesToSkip());
		parent.addSettingsMessage("   ---Pulses Skipped: " + autoSkipPulseListener.getCurrentPulseSkipped());
	}
	
	@Override
	public KeyEvent getNextCommand() {
		_init();
		
		if (autoSkipPulseListener.isNewPulse()) {
			return _getNextCommand();
		} else {
			return null;
		}
	}

	protected void _init() {
		if (autoSkipPulseListener == null) {
			autoSkipPulseListener = new PulseListener(parent, getDefaultPulsesToSkip());
		}
	}

}
