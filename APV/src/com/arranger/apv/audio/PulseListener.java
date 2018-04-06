package com.arranger.apv.audio;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.FrameSkipper;
import com.arranger.apv.Main;

import ddf.minim.analysis.BeatDetect;

/**
 * TODO change to fade out pulses not frames?
 */
public class PulseListener extends APVPlugin {

	public static final int DEFAULT_FADE_OUT_FRAMES = 100;
	public static final int DEFAULT_PULSES_TO_SKIP = 4;
	
	private BeatDetect pulseDetector;
	private int lastFrame = 0;
	private int numFramesToFade;
	private int pulsesToSkip = DEFAULT_PULSES_TO_SKIP;
	private int currentPulseSkipped = 0;
	private FrameSkipper frameSkipper;
	
	public PulseListener(Main parent) {
		this(parent, DEFAULT_FADE_OUT_FRAMES, DEFAULT_PULSES_TO_SKIP);
	}
	
	public PulseListener(Main parent, int fadeOutFrames, int pulsesToSkip) {
		super(parent);
		this.numFramesToFade = fadeOutFrames;
		this.pulsesToSkip = pulsesToSkip;
		currentPulseSkipped = 0;
		frameSkipper = new FrameSkipper(parent);
		pulseDetector = parent.getAudio().getBeatInfo().getPulseDetector();
	}
	
	public boolean isPulse() {
		return getPctPulse() < .0001f;
	}
	
	public boolean isNewPulse() {
		return getPctPulse() > .999f;
	}
	
	public float getPctPulse() {
		float result = 0f;
		int currentFrame = parent.frameCount;
		if (pulseDetector.isOnset()) {
			
			//Don't increment this count if we've already checked this frame
			if (frameSkipper.isNewFrame()) {
				currentPulseSkipped++;
			}
			
			if (currentPulseSkipped % pulsesToSkip == 0) {
				currentPulseSkipped = 0;
				lastFrame = currentFrame;
				result = 1f;
				parent.addDebugMsg("  --PulseOnset");
			}
		} else {
			int numFramesSinceOnset = currentFrame - lastFrame;
			if (numFramesSinceOnset < numFramesToFade) {
				//still fading
				result = (float)numFramesSinceOnset / (float)numFramesToFade;
			}
		}
		
		return result;
	}
	
	public int getPulsesToSkip() {
		return pulsesToSkip;
	}

	public int getCurrentPulseSkipped() {
		return currentPulseSkipped;
	}
}