package com.arranger.apv.agent;

import java.util.Arrays;

import com.arranger.apv.Main;
import com.arranger.apv.Switch;
import com.arranger.apv.util.SafePainter;
import com.arranger.apv.util.TextDrawHelper;

public class DebugPulseAgent extends PulseAgent {

	
	public DebugPulseAgent(Main parent) {
		super(parent, 1);
	}


	@Override
	protected void onPulse() {
		Switch sw = parent.getSwitches().get(Main.SWITCH_NAMES.DEBUG_PULSE.name);
		if (sw.isEnabled()) {
			String msg = String.valueOf(parent.getFrameCount());
			new TextDrawHelper(parent, 10, Arrays.asList(new String[] {msg}), SafePainter.LOCATION.UPPER_RIGHT);
		}
	}
}
