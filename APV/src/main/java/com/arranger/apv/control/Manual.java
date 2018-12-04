package com.arranger.apv.control;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.event.EventTypes;

public class Manual extends ControlSystem {
	
	protected boolean mouseDown = false;

	public Manual(Main parent) {
		super(parent);
	}

	@Override
	public CONTROL_MODES getControlMode() {
		return CONTROL_MODES.MANUAL;
	}

	@Override
	public Command getNextCommand() {
		Command result = null;
		
		if (parent.mousePressed && !mouseDown) {
			//start of a press
			mouseDown = true;
		} else if (!parent.mousePressed && mouseDown) {
			//fire in the hole
			Command.FIRE_EVENT.setPrimaryArg(EventTypes.MOUSE_PULSE.name());
			result = Command.FIRE_EVENT;
			mouseDown = false;
		}
		
		return result;
	}
}
