package com.arranger.apv.util.frame;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

/**
 * Use this class when you only want to perform an action once / frame
 * This class is very stateful, and shouldn't be shared by any other client.
 * 
 * Usage:
 * 		//only want to respond once / frame
		if (frameSkipper.isNewFrame()) {
			lastAnswer = _isSnap();
		}
		return lastAnswer;
 * 
 */
public class SingleFrameSkipper extends APVPlugin {
	protected int lastFrameSkipped = -1;
	
	public SingleFrameSkipper(Main parent) {
		super(parent);
	}

	public boolean isNewFrame() {
		int currentFrame = parent.getFrameCount();
		if (lastFrameSkipped != currentFrame) {
			lastFrameSkipped = currentFrame;
			return true;
		} else {
			return false;
		}
	}
	
	public int getLastFrameSkipped() {
		return lastFrameSkipped;
	}
}