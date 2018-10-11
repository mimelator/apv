package com.arranger.apv.control;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public class Manual extends ControlSystem {

	public Manual(Main parent) {
		super(parent);
	}

	@Override
	public CONTROL_MODES getControlMode() {
		return CONTROL_MODES.MANUAL;
	}

	@Override
	public Command getNextCommand() {
		return null;
	}
	
	@Override
	public boolean allowsMouseLocation() {
		return false;
	}
}
