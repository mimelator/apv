package com.arranger.apv;

public class FrameSkipper extends APVPlugin {
	private int lastPulseFrameSkipped = 0;
	
	public FrameSkipper(Main parent) {
		super(parent);
	}

	public boolean isNewFrame() {
		int currentFrame = parent.frameCount;
		if (lastPulseFrameSkipped != currentFrame) {
			lastPulseFrameSkipped = currentFrame;
			return true;
		} else {
			return false;
		}
	}
}