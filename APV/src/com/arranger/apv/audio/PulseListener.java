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
	private int currentPulseSkipped;
	private SingleFrameSkipper frameSkipper;
	private FrameFader frameFader;
	
	public PulseListener(Main parent) {
		this(parent, DEFAULT_FADE_OUT_FRAMES, DEFAULT_PULSES_TO_SKIP);
	}
	
	public PulseListener(Main parent, int fadeOutFrames, int pulsesToSkip) {
		super(parent);
		this.pulsesToSkip = pulsesToSkip;
		currentPulseSkipped = 0;
		
		//need to trigger the frameFader when ever the pulseDetector returns true
		frameFader = new FrameFader(parent, fadeOutFrames);
		frameSkipper = new MultiFrameSkipper(parent, pulsesToSkip);
		pulseDetector = parent.getAudio().getBeatInfo().getPulseDetector();
	}
	
	public boolean isPulse() {
		float pctPulse = getPctPulse(); //side-effects
		debug("PulseListener#isPulse getPulse: " + pctPulse);
		return frameFader.isFadeActive();
	}
	
	public boolean isNewPulse() {
		float pctPulse = getPctPulse(); //side-effects
		debug("PulseListener#isNewPulse getPulse: " + pctPulse);
		return frameFader.isFadeNew();
	}
	
	public float getPctPulse() {
		debug("PulseListener#getPctPulse: frameCount: " + parent.getFrameCount());
		
		float result = 0f;
		boolean onset = pulseDetector.isOnset();
		debug("PulseListener#getPctPulse: pulseDetector.isOnset: " + onset);
		if (onset) {
			//Don't increment this count if we've already checked this frame
			boolean newFrame = frameSkipper.isNewFrame();
			debug("PulseListener#getPctPulse: frameSkipper.isNewFrame: " + newFrame);
			if (newFrame) {
				frameFader.startFade();
				debug("PulseListener#getPctPulse -> pulseDetector.isOnset && frameSkipper.isNewFrame");
				currentPulseSkipped = 0;
				result = 1f;
				parent.addDebugMsg("  --PulseOnset");
				debug("  --PulseOnset");
			} else {
				currentPulseSkipped++;
			}
		} else {
			result = frameFader.getFadePct();
		}
		
		debug("PulseListener#getPctPulse: returns: " + result);
		
		return result;
	}
	
	public int getPulsesToSkip() {
		return pulsesToSkip;
	}

	public int getCurrentPulseSkipped() {
		return currentPulseSkipped;
	}
}