package com.arranger.apv.control;

import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem;

import processing.event.KeyEvent;

public abstract class PulseListeningControlSystem extends ControlSystem {
	
	protected PulseListener autoSkipPulseListener;

	public PulseListeningControlSystem(Main parent) {
		super(parent);
		
		autoSkipPulseListener = new PulseListener(parent, getDefaultPulsesToSkip());
		
		parent.getSetupEvent().register(() -> {
			CommandSystem cs = parent.getCommandSystem();
			cs.registerHandler(Command.PULSE_SKIP_INC,
					event -> autoSkipPulseListener.incrementPulsesToSkip());
			cs.registerHandler(Command.PULSE_SKIP_DEC,
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
