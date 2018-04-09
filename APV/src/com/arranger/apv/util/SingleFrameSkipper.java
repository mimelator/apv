package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

/**
 * Use this class when you only want to perform an action once / frame
 * This class is very stateful, and shouldn't be shared by any other client
 */
public class SingleFrameSkipper extends APVPlugin {
	protected int lastPulseFrameSkipped = -1;
	
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