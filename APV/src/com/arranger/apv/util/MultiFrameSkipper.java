package com.arranger.apv.util;

import com.arranger.apv.Main;

/**
 * This class is very stateful, and shouldn't be shared by any other client
 * 
 * It doesn't need to be reset, it just keeps cycling
 */
public class MultiFrameSkipper extends SingleFrameSkipper {

	protected int framesToSkip;
	
	public MultiFrameSkipper(Main parent, int framesToSkip) {
		super(parent);
		this.framesToSkip = framesToSkip;
	}

	@Override
	public boolean isNewFrame() {
		boolean newFrame = super.isNewFrame();
		int frameCount = parent.getFrameCount();
		boolean frameHasCycled = (frameCount % framesToSkip) == 0;
		return newFrame && frameHasCycled;
	}
	
	public void reset(int framesToSkip) {
		lastPulseFrameSkipped = parent.getFrameCount();
		this.framesToSkip = framesToSkip;
	}
}
