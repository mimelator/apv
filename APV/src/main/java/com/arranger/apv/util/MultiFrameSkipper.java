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
		reset(framesToSkip);
	}
	
	public int getFramesToSkip() {
		return framesToSkip;
	}

	@Override
	public boolean isNewFrame() {
		int frameCount = parent.getFrameCount();
		boolean frameHasCycled = (frameCount % framesToSkip) == 0;
		boolean newFrame = super.isNewFrame();
		
		if (frameCount - lastFrameSkipped > framesToSkip) {
			if (frameHasCycled == false) {
				//I don't think this should happen
				throw new IllegalStateException();
			}
		}
		
		if (newFrame && frameHasCycled) {
			reset(framesToSkip);
			return true;
		} else {
			return false;
		}
	}
	
	public void reset(int framesToSkip) {
		lastFrameSkipped = parent.getFrameCount();
		this.framesToSkip = framesToSkip;
	}
}
