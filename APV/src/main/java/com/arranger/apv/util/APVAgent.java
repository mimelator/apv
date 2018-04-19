package com.arranger.apv.util;

import com.arranger.apv.APVCallback;
import com.arranger.apv.Main;
import com.arranger.apv.ControlSystem.CONTROL_MODES;

public class APVAgent extends APVCallback {

	public APVAgent(Main parent) {
		super(parent, "agents");
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && 
				!CONTROL_MODES.MANUAL.equals(parent.getCurrentControlMode());
	}
}
