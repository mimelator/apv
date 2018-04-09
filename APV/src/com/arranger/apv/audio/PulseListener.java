package com.arranger.apv.audio;

import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.FrameFader;
import com.arranger.apv.util.MultiFrameSkipper;

import ddf.minim.analysis.BeatDetect;

/**
 * TODO change to fade out pulses not frames?
 */
public class PulseListener extends APVPlugin {
	
	private static final Logger logger = Logger.getLogger(PulseListener.class.getName());

	public static final int DEFAULT_FADE_OUT_FRAMES = 100;
	public static final int DEFAULT_PULSES_TO_SKIP = 4;
	
	private BeatDetect pulseDetector;
	private int pulsesToSkip = DEFAULT_PULSES_TO_SKIP;
	private int currentPulseSkipped;
	private MultiFrameSkipper frameSkipper;
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
		pulseDetector = parent.getAudio().getBeatInfo().getPulseDetector();
		newPulse();
	}
	
	public boolean isPulse() {
		float pctPulse = getPctPulse(); //side-effects
		logger.fine("pctPulse: " + pctPulse);
		return frameFader.isFadeActive();
	}
	
	public boolean isNewPulse() {
		float pctPulse = getPctPulse(); //side-effects
		logger.fine("pctPulse: " + pctPulse);
		return frameFader.isFadeNew();
	}
	
	public float getPctPulse() {
		logger.fine("frameCount: " + parent.getFrameCount());
		
		float result = 0f;
		boolean onset = pulseDetector.isOnset();
		logger.fine("isOnset: " + onset);
		if (onset) {
			//Don't increment this count if we've already checked this frame
			boolean newFrame = frameSkipper.isNewFrame();
			logger.fine("frameSkipper.isNewFrame: " + newFrame);
			if (newFrame) {
				frameFader.startFade();
				logger.fine("pulseDetector.isOnset && frameSkipper.isNewFrame");
				currentPulseSkipped = 0;
				result = 1f;
				parent.addSettingsMessage("  --PulseOnset");
				logger.fine("  --PulseOnset");
			} else {
				currentPulseSkipped++;
			}
		} else {
			result = frameFader.getFadePct();
		}
		
		logger.fine("returns: " + result);
		
		return result;
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
	
	private void newPulse() {
		frameSkipper = new MultiFrameSkipper(parent, pulsesToSkip);
		frameFader.startFade();
	}
}