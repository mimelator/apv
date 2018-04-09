package com.arranger.apv.control;

import com.arranger.apv.Main;

import processing.event.KeyEvent;

public class Auto extends PulseListeningControlSystem {
	
	private static final int DEFAULT_PULSES_TO_SKIP_FOR_AUTO = 16;
	
	public Auto(Main parent) {
		super(parent);
	}

	@Override
	public CONTROL_MODES getControlMode() {
		return CONTROL_MODES.AUTO;
	}
	
	@Override
	protected int getDefaultPulsesToSkip() {
		return DEFAULT_PULSES_TO_SKIP_FOR_AUTO;
	}

	@Override
	protected KeyEvent _getNextCommand() {
		return createScramble();
	}
}
