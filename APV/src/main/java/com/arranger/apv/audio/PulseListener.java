package com.arranger.apv.audio;

import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.frame.MultiFrameSkipper;

import ddf.minim.analysis.BeatDetect;

/**
 * Every pulsesToSkip is reached answer true to the {@link #isNewPulse()}
 */
public class PulseListener extends APVPlugin {
	
	private static final Logger logger = Logger.getLogger(PulseListener.class.getName());

	public static final int DEFAULT_FADE_OUT_FRAMES = 100;
	public static final int DEFAULT_PULSES_TO_SKIP = 4;
	
	private int pulsesToSkip = DEFAULT_PULSES_TO_SKIP;
	private int currentPulseSkipped;
	private MultiFrameSkipper frameSkipper;
	
	
	public PulseListener(Main parent) {
		this(parent, DEFAULT_PULSES_TO_SKIP);
	}
	
	public PulseListener(Main parent, int pulsesToSkip) {
		this(parent, pulsesToSkip, true);
	}
	
	public PulseListener(Main parent, int pulsesToSkip, boolean startPulse) {
		super(parent);
		this.pulsesToSkip = pulsesToSkip;
		currentPulseSkipped = 0;
		
		//need to trigger the frameFader when ever the pulseDetector returns true
		if (startPulse) {
			newPulse();
		}
	}
	
	public void addSettingsMessages() {
		parent.addSettingsMessage("   ---Pulses to Skip: " + getPulsesToSkip());
		parent.addSettingsMessage("   ---Pulses Skipped: " + getCurrentPulseSkipped());
	}
	
	public boolean isNewPulse() {
		if (hasExceededPulsesToSkip()) {
			return true;
		}
		
		if (frameSkipper == null) {
			return false;
		}
		
		logger.fine("frameCount: " + parent.getFrameCount());
		
		BeatDetect pulseDetector = parent.getAudio().getBeatInfo().getPulseDetector();
		boolean onset = pulseDetector.isOnset();
		logger.fine("isOnset: " + onset);
		if (onset) {
			//Don't increment this count if we've already checked this frame
			boolean newFrame = frameSkipper.isNewFrame();
			logger.fine("frameSkipper.isNewFrame: " + newFrame);
			if (newFrame) {
				logger.fine("  --PulseOnset");
				currentPulseSkipped = 0;
				return true;
			} else {
				currentPulseSkipped++;
			}
		}
		
		logger.fine("returns: " + false);
		return false;
	}
	
	public void adjustPulsesToSkip(int adjust) {
		pulsesToSkip += adjust;
		if (pulsesToSkip < 1) {
			pulsesToSkip = 1;
		}
		newPulse();
	}
	
	public void incrementPulsesToSkip() {
		pulsesToSkip++;
		newPulse();
	}
	
	public void deccrementPulsesToSkip() {
		pulsesToSkip--;
		newPulse();
	}
	
	public int getPulsesToSkip() {
		return pulsesToSkip;
	}

	public int getCurrentPulseSkipped() {
		return currentPulseSkipped;
	}
	
	private boolean hasExceededPulsesToSkip() {
		boolean b = currentPulseSkipped > pulsesToSkip;
		if (b) {
			newPulse();
		}
		return b; 
	}
	
	private void newPulse() {
		if (frameSkipper != null) {
			frameSkipper.reset(pulsesToSkip);
		} else {
			frameSkipper = new MultiFrameSkipper(parent, pulsesToSkip);
		}
		currentPulseSkipped = 0;
	}
}