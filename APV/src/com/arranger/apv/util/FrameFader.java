package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class FrameFader extends APVPlugin {
	
	private int numFramesToFade;
	private int lastFrame = 0;

	public FrameFader(Main parent) {
		super(parent);
	}
	
	public FrameFader(Main parent, int numFramesToFade) {
		super(parent);
		this.numFramesToFade = numFramesToFade;
	}
	
	public int getNumFramesToFade() {
		return numFramesToFade;
	}

	public void setNumFramesToFade(int numFramesToFade) {
		this.numFramesToFade = numFramesToFade;
	}

	public void startFade() {
		lastFrame = parent.getFrameCount();
	}

	public boolean isFadeActive() {
		return getFadePct() > 0;
	}
	
	/**
	 * 1/10th into the fade pct returns 90%
	 * @return
	 */
	public float getFadePct() {
		int currentFrame = parent.getFrameCount();
		
		int numFramesSinceOnset = currentFrame - lastFrame;
		if (numFramesSinceOnset == 0) {
			return 1; //started this frame
		} else if (numFramesSinceOnset < numFramesToFade) {
			//still fading
			return (float)(numFramesToFade - numFramesSinceOnset) / numFramesToFade;
		}
		return 0;
	}
}
