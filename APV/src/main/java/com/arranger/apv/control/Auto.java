package com.arranger.apv.control;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public class Auto extends Manual {
	
	
	public Auto(Main parent) {
		super(parent);
	}

	@Override
	public CONTROL_MODES getControlMode() {
		return CONTROL_MODES.AUTO;
	}
	
	@Override
	public Command getNextCommand() {
		return null;
	}
}
