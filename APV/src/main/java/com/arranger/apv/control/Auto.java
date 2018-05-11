package com.arranger.apv.control;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

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
	protected Command _getNextCommand() {
		return Command.SCRAMBLE;
	}
}
