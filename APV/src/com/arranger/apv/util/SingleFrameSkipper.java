package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

/**
 * Use this class when you only want to perform an action once / frame
 */
public class SingleFrameSkipper extends APVPlugin {
	private int lastPulseFrameSkipped = 0;
	
	public SingleFrameSkipper(Main parent) {
		super(parent);
	}

	public boolean isNewFrame() {
		int currentFrame = parent.getFrameCount();
		if (lastPulseFrameSkipped != currentFrame) {
			lastPulseFrameSkipped = currentFrame;
			return true;
		} else {
			return false;
		}
	}
}