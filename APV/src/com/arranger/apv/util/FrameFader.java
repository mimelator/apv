package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

/**
 * The multi frame skipper will only answer true to isNewFrame#every few frames
 * This class is very stateful, and shouldn't be shared by any other client
 */
public class FrameFader extends APVPlugin {
	
	private static final float IS_NEW_FRAME_SIGNAL = -1;
	
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
		float fadePct = getFadePct();
		if (IS_NEW_FRAME_SIGNAL == fadePct) {
			return true;
		} else {
			return fadePct > 0;	
		}
	}
	
	public boolean isFadeNew() {
		return getFadePct() == IS_NEW_FRAME_SIGNAL;
	}
	
	/**
	 * 1/10th into the fade pct returns 90%
	 * @return
	 */
	public float getFadePct() {
		int currentFrame = parent.getFrameCount();
		
		int numFramesSinceOnset = currentFrame - lastFrame;
		if (numFramesSinceOnset == 0) {
			return IS_NEW_FRAME_SIGNAL; //started this frame
		} else if (numFramesSinceOnset < numFramesToFade) {
			//still fading
			return (float)(numFramesToFade - numFramesSinceOnset) / numFramesToFade;
		}
		return 0;
	}
}
