package com.arranger.apv.audio;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.FrameFader;
import com.arranger.apv.util.MultiFrameSkipper;
import com.arranger.apv.util.SingleFrameSkipper;

import ddf.minim.analysis.BeatDetect;

/**
 * TODO change to fade out pulses not frames?
 */
public class PulseListener extends APVPlugin {

	public static final int DEFAULT_FADE_OUT_FRAMES = 100;
	public static final int DEFAULT_PULSES_TO_SKIP = 4;
	
	private BeatDetect pulseDetector;
	private int pulsesToSkip = DEFAULT_PULSES_TO_SKIP;
	private int currentPulseSkipped = 0;
	private SingleFrameSkipper frameSkipper;
	private FrameFader frameFader;
	
	public PulseListener(Main parent) {
		this(parent, DEFAULT_FADE_OUT_FRAMES, DEFAULT_PULSES_TO_SKIP);
	}
	
	public PulseListener(Main parent, int fadeOutFrames, int pulsesToSkip) {
		super(parent);
		this.pulsesToSkip = pulsesToSkip;
		currentPulseSkipped = 0;
		frameFader = new FrameFader(parent, fadeOutFrames);
		frameSkipper = new MultiFrameSkipper(parent, pulsesToSkip);
		pulseDetector = parent.getAudio().getBeatInfo().getPulseDetector();
	}
	
	public boolean isPulse() {
		return getPctPulse() < .0001f;
	}
	
	public boolean isNewPulse() {
		return getPctPulse() > .999f;
	}
	
	public float getPctPulse() {
		if (!frameFader.isFadeActive()) {
			return 0;
		}
		
		float result = 0f;
		if (pulseDetector.isOnset()) {
			//Don't increment this count if we've already checked this frame
			if (frameSkipper.isNewFrame()) {
				currentPulseSkipped = 0;
				result = 1f;
				parent.addDebugMsg("  --PulseOnset");
			} else {
				currentPulseSkipped++;
			}
		} else {
			result = frameFader.getFadePct();
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