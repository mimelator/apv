package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;

/**
 * This is very stateful.  Don't share
 * Once every N pulses, reverse direction
 */
public class Reverser extends APVPlugin {
	
	private PulseListener pulseListener;
	private SingleFrameSkipper frameSkipper;
	private boolean reverse = false;
	
	public Reverser(Main parent, int numPulses) {
		super(parent);
		
		pulseListener = new PulseListener(parent, 1, numPulses); //Direction Change every two pulses
		frameSkipper = new SingleFrameSkipper(parent);
	}

	public boolean isReverse() {
		boolean newFrame = frameSkipper.isNewFrame();
		boolean newPulse = pulseListener.isNewPulse();
		
		debug("Reverser [newFrame, newPulse]: [" + newFrame + ", " + newPulse + "]");
		
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
