package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class PulseFader extends APVPlugin {

	private FrameFader fader;
	float pulseVal, normalVal;
	
	public PulseFader(Main parent, int numFramesToFader, float pulseVal, float normalVal) {
		super(parent);
		this.pulseVal = pulseVal;
		this.normalVal = normalVal;
		
		fader = new FrameFader(parent, numFramesToFader);
		parent.getPulseListener().registerPulseListener(()  -> {
			fader.startFade();
		});
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
