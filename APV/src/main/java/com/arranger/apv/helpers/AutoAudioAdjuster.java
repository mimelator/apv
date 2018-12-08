package com.arranger.apv.helpers;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public class AutoAudioAdjuster extends APVPlugin {
	
	protected static final int DEFAULT_CHECKS_PER_RESET = 10;
	
	protected float targetCmdsPerSec;
	protected int numChecks;

	public AutoAudioAdjuster(Main parent) {
		super(parent);
	}
	
	public void adjustToLevel() {
		float cmdsPerSec = parent.getVideoGameHelper().getCheckPointCommandsPerSec();
		
		if (cmdsPerSec < targetCmdsPerSec) {
			fireCommand(Command.AUDIO_INC);
		} else {
			fireCommand(Command.AUDIO_DEC);
		}
		
		if (++numChecks % DEFAULT_CHECKS_PER_RESET == 0) {
			parent.getVideoGameHelper().resetCheckPoint();
		}
	}

	protected void fireCommand(Command cmd) {
		parent.getCommandSystem().invokeCommand(cmd, getDisplayName(), 0);
	}
	
	public float getTargetCmdsPerSec() {
		return targetCmdsPerSec;
	}

	public void setTargetCmdsPerSec(float targetCmdsPerSec) {
		this.targetCmdsPerSec = targetCmdsPerSec;
	}
}
