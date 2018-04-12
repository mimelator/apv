package com.arranger.apv.util;

import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;

/**
 * This is very stateful.  Don't share
 * Once every N pulses, reverse direction
 */
public class Reverser extends APVPlugin {
	
	private static final Logger logger = Logger.getLogger(Reverser.class.getName());
	
	private PulseListener pulseListener;
	private SingleFrameSkipper frameSkipper;
	private boolean reverse = false;
	
	public Reverser(Main parent, int numPulses) {
		super(parent);
		
		pulseListener = new PulseListener(parent, numPulses); //Direction Change every numPulses
		frameSkipper = new SingleFrameSkipper(parent);
	}

	/**
	 * Only automatically reverse once / frame
	 */
	public boolean isReverse() {
		boolean newFrame = frameSkipper.isNewFrame();
		boolean newPulse = pulseListener.isNewPulse();
		
		logger.fine("Reverser [newFrame, newPulse]: [" + newFrame + ", " + newPulse + "]");
		
		if (newFrame && newPulse) {
			reverse = !reverse;
		}
		
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}
	
	public void reverse() {
		this.reverse = !reverse;
	}
}
