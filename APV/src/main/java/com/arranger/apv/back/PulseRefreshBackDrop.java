package com.arranger.apv.back;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;
import com.arranger.apv.util.Configurator;

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
	
	public PulseRefreshBackDrop(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, PulseListener.DEFAULT_PULSES_TO_SKIP));
	}
	
	@Override
	public String getConfig() {
		//{PulseRefreshBackDrop : [${50_PCT_DEFAULT_PULSES_TO_SKIP}]}
		return String.format("{%1s : [%2d]}", getName(), pulse.getPulsesToSkip());
	}
	
	@Override
	public void drawBackground() {
		boolean newPulse = pulse.isNewPulse();
		if (newPulse) {
			parent.background(Color.BLACK.getRGB());
		}
		
		parent.addSettingsMessage("  --newPulse: " + newPulse);
		pulse.addSettingsMessages();
	}
}
