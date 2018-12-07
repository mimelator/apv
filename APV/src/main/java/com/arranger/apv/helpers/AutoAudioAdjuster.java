package com.arranger.apv.helpers;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public class AutoAudioAdjuster extends APVPlugin {
	
	protected static final int DEFAULT_CHECKS_PER_RESET = 10;
	
	public static enum LEVEL {
		Low, Medium, High, Off
	}
	
	protected float cmdsTargetLow, cmdsTargetHigh;
	protected LEVEL level;
	protected int numChecks;

	public AutoAudioAdjuster(Main parent) {
		super(parent);
		level = LEVEL.High;
	}
	
	public void adjustToLevel() {
		if (level == LEVEL.Off) {
			return;
		}
		
		float cmdsPerSec = parent.getVideoGameHelper().getCheckPointCommandsPerSec();
		float targetCmdsPerSec = getTargetCommandsPerSec();
		
//		System.out.format("frame: %s cmds vs target: %s vs %s\n", parent.frameCount, cmdsPerSec, targetCmdsPerSec);
		
		if (cmdsPerSec < targetCmdsPerSec) {
			fireCommand(Command.AUDIO_INC);
		} else {
			fireCommand(Command.AUDIO_DEC);
		}
		
//		System.out.println("DB: " + parent.getAudio().getDB());
		
		if (++numChecks % DEFAULT_CHECKS_PER_RESET == 0) {
//			System.out.println("Resetting check point");
			parent.getVideoGameHelper().resetCheckPoint();
		}
	}

	public LEVEL getLevel() {
		return level;
	}

	public void setLevel(LEVEL level) {
		this.level = level;
	}
	
	public void setTargets(float cmdsTargetLow, float cmdsTargetHigh) {
		this.cmdsTargetLow = cmdsTargetLow;
		this.cmdsTargetHigh = cmdsTargetHigh;
	}
	
	protected void fireCommand(Command cmd) {
		parent.getCommandSystem().invokeCommand(cmd, getDisplayName(), 0);
	}
	
	protected float getTargetCommandsPerSec() {
		System.out.printf("Level: %s, cmdsLow: %s, cmdsHigh: %s\n", level, cmdsTargetLow, cmdsTargetHigh);
		
		switch (level) {
		case Low:
			return cmdsTargetLow;
		case Medium:
			return (cmdsTargetHigh + cmdsTargetLow) / 2.0f;
		case High:
			return cmdsTargetHigh;
		default:
			return 0;
		}
	}
}
