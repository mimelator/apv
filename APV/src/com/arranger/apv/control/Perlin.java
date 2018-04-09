package com.arranger.apv.control;

import com.arranger.apv.ControlSystem;
import com.arranger.apv.Main;

import processing.event.KeyEvent;

public class Perlin extends ControlSystem {

	public Perlin(Main parent) {
		super(parent);
	}

	@Override
	public CONTROL_MODES getControlMode() {
		return CONTROL_MODES.PERLIN;
	}

	@Override
	public KeyEvent getNextCommand() {
		return null;
	}

}
