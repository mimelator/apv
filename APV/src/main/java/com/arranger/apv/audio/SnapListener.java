package com.arranger.apv.audio;

import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.frame.QuietWindow;
import com.arranger.apv.util.frame.SingleFrameSkipper;

import ddf.minim.analysis.BeatDetect;

public class SnapListener extends APVPlugin {
	
	private static final Logger logger = Logger.getLogger(SnapListener.class.getName());

	protected SingleFrameSkipper frameSkipper;
	protected QuietWindow quietWindow;
	protected int framesToSkip;
	
	public SnapListener(Main parent, int framesToSkip) {
		super(parent);
		this.framesToSkip = framesToSkip;
		frameSkipper = new SingleFrameSkipper(parent);
		quietWindow = new QuietWindow(parent, framesToSkip);
	}

	protected boolean lastAnswer = false;
	
	public boolean isSnap() {
		//only want to respond once / frame
		if (frameSkipper.isNewFrame()) {
			lastAnswer = _isSnap();
		}
		
		return lastAnswer;
	}
	
	protected boolean _isSnap() {
		if (quietWindow.isInQuietWindow()) {
			return false;
		}
		
		BeatDetect fd = parent.getAudio().getBeatInfo().getFreqDetector();
		
		//This snap detection will look for a large result in the top few bands of the spectrum
		int size = fd.detectSize();
		int lower = (int)(size * .7f);
		
		boolean range = fd.isRange(lower, size - 1, 1);
		if (range) {
			reset(framesToSkip);
			logger.info("Found a Snap at frame: " + parent.getFrameCount());
		}
		return range;
	}

	public void incrementFramesToSkip() {
		reset(framesToSkip++);
	}
	
	public void deccrementFramesToSkip() {
		reset(framesToSkip--);
	}
	
	public int getFramesToSkip() {
		return framesToSkip;
	}

	public int getCurrentFramesSkipped() {
		int lastFrameSkipped = framesToSkip;
		return parent.getFrameCount() - lastFrameSkipped;
	}
	
	protected void reset(int framesToSkip) {
		quietWindow.reset(framesToSkip);
	}
}
