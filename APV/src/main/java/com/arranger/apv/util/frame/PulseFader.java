package com.arranger.apv.util.frame;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;

public class PulseFader extends APVPlugin {

	private FrameFader fader;
	float pulseVal, normalVal;
	int pulsesToSkip;
	
	public PulseFader(Main parent, int numFramesToFade, float pulseVal, float normalVal) {
		this(parent, numFramesToFade, pulseVal, normalVal, PulseListener.DEFAULT_FADE_OUT_FRAMES);
	}
	
	public PulseFader(Main parent, int numFramesToFade, float pulseVal, float normalVal, int pulsesToSkip) {
		super(parent);
		this.pulseVal = pulseVal;
		this.normalVal = normalVal;
		this.pulsesToSkip = pulsesToSkip;
		
		fader = new FrameFader(parent, numFramesToFade);
		parent.getSetupEvent().register(() -> {
			parent.getPulseListener().registerHandler(() -> {
				fader.startFade();
			}, pulsesToSkip, null);
		});
	}
	
	public int getPulsesToSkip() {
		return pulsesToSkip;
	}

	public float getValue() {
		if (!fader.isFadeActive()) {
			return normalVal;
		} else if (fader.isFadeNew()) {
			return pulseVal;
		} else {
			return pulseVal * fader.getFadePct(); 
		}
	}
}
