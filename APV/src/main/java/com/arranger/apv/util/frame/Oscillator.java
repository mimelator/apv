package com.arranger.apv.util.frame;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import processing.core.PApplet;

public class Oscillator extends APVPlugin {
	
	private static final int TARGET_FRAME_RATE_FOR_OSC = 30;

	private int targetFrameRate = TARGET_FRAME_RATE_FOR_OSC;
	
	public Oscillator(Main parent) {
		super(parent);
	}

	/**
	 * This little tool will keep interpolating between the low and high values based
	 * upon the frameCount.  It should complete a circuit every
	 * 
	 * @param cycleTime the lower the number the faster the cycling.  Typically between : 4 and 20
	 */
	public float oscillate(float low, float high, float cycleTime) {
		float fr = targetFrameRate; //frameRate
		float cos = PApplet.cos(PI * parent.getFrameCount() / fr / cycleTime);
		return parent.mapEx(cos, -1, 1, low, high); 
	}
	
	@FunctionalInterface
	public static interface Listener {
		void onOscillate();
	}
	
	public float oscillate(float low, float high, float cycleTime, Listener l) {
		float result = oscillate(low, high, cycleTime);
		if (parent.getFrameCount() % (targetFrameRate * (cycleTime / 2)) == 0) {
			l.onOscillate();
		}
		return result;
	}
	
	
	public int getTargetFrameRate() {
		return targetFrameRate;
	}

	public void setTargetFrameRate(int targetFrameRate) {
		this.targetFrameRate = targetFrameRate;
	}
}
