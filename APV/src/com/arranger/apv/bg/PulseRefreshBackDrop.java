package com.arranger.apv.bg;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;

public class PulseRefreshBackDrop extends BackDropSystem {

	private PulseListener pulse;

	public PulseRefreshBackDrop(Main parent) {
		super(parent);
		pulse = new PulseListener(parent);
	}
	
	public PulseRefreshBackDrop(Main parent, int fadeOutFrames, int pulsesToSkip) {
		super(parent);
		pulse = new PulseListener(parent, fadeOutFrames, pulsesToSkip);
	}
	
	@Override
	public void drawBackground() {
		
		if (pulse.isPulse()) {
			parent.background(Color.BLACK.getRGB());
		}
		
		parent.addDebugMsg("  --currentPulseSkipped: " + pulse.getCurrentPulseSkipped());
		parent.addDebugMsg("  --pulsesToSkip: " + pulse.getPulsesToSkip());
	}
}
