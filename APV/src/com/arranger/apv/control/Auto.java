package com.arranger.apv.control;

import com.arranger.apv.CommandSystem;
import com.arranger.apv.ControlSystem;
import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;

import processing.event.KeyEvent;

public class Auto extends ControlSystem {
	
	private static final int DEFAULT_PULSES_TO_SKIP_FOR_AUTO = 16;

	private PulseListener autoSkipPulseListener;
	
	public Auto(Main parent) {
		super(parent);
		autoSkipPulseListener = new PulseListener(parent, DEFAULT_PULSES_TO_SKIP_FOR_AUTO);
		
		CommandSystem cs = parent.getCommandSystem();
		cs.registerCommand(']', "Pulse++", "Increases the number of pulses to skip in Auto mode", event -> autoSkipPulseListener.incrementPulsesToSkip());
		cs.registerCommand('[', "Pulse--", "Deccreases the number of pulses to skip in Auto mode", event -> autoSkipPulseListener.deccrementPulsesToSkip());
	}

	@Override
	public CONTROL_MODES getControlMode() {
		return CONTROL_MODES.AUTO;
	}

	@Override
	public boolean allowsMouseLocation() {
		return false;
	}
	
	@Override
	public void addSettingsMessages() {
		parent.addSettingsMessage("   ---Pulses to Skip: " + autoSkipPulseListener.getPulsesToSkip());
		parent.addSettingsMessage("   ---Pulses Skipped: " + autoSkipPulseListener.getCurrentPulseSkipped());
	}

	@Override
	public KeyEvent getNextCommand() {
		if (autoSkipPulseListener.isNewPulse()) {
			return createScramble();
		} else {
			return null;
		}
	}
}
