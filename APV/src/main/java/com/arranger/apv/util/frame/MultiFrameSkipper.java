package com.arranger.apv.util.frame;

import com.arranger.apv.Main;

/**
 * This class is very stateful, and shouldn't be shared by any other client
 * 
 * It doesn't need to be reset, it just keeps cycling
 * 
 * Use this class when you want to perform an action just once every few frames
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
		framesToSkip = (framesToSkip < 1) ? 1 : framesToSkip;
		
		lastFrameSkipped = parent.getFrameCount();
		this.framesToSkip = framesToSkip;
	}
}
