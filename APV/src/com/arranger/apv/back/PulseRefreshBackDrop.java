package com.arranger.apv.back;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;

public class PulseRefreshBackDrop extends BackDropSystem {

	private PulseListener pulse;

	public PulseRefreshBackDrop(Main parent) {
		super(parent);
		pulse = new PulseListener(parent);
	}
	
	public PulseRefreshBackDrop(Main parent, int pulsesToSkip) {
		super(parent);
		pulse = new PulseListener(parent, pulsesToSkip);
	}
	
	@Override
	public void drawBackground() {
		
		if (pulse.isNewPulse()) {
			parent.background(Color.BLACK.getRGB());
		}
		
		parent.addSettingsMessage("  --currentPulseSkipped: " + pulse.getCurrentPulseSkipped());
		parent.addSettingsMessage("  --pulsesToSkip: " + pulse.getPulsesToSkip());
	}
}
