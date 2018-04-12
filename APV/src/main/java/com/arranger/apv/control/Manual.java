package com.arranger.apv.control;

import com.arranger.apv.ControlSystem;
import com.arranger.apv.Main;

import processing.event.KeyEvent;

public class Manual extends ControlSystem {

	public Manual(Main parent) {
		super(parent);
	}

	@Override
	public CONTROL_MODES getControlMode() {
		return CONTROL_MODES.MANUAL;
	}

	@Override
	public KeyEvent getNextCommand() {
		return null;
	}
}
