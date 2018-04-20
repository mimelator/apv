package com.arranger.apv.color;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

/**
 * Basic BeatColor System toggles between red & white
 */
public class BeatColorSystem extends ColorSystem {
	
	private Color primary, pulse;
	
	public BeatColorSystem(Main parent) {
		this(parent, Color.WHITE, Color.RED);
	}
	
	public BeatColorSystem(Main parent, Color primary, Color pulse) {
		super(parent);
		this.primary = primary;
		this.pulse = pulse;
	}
	
	public BeatColorSystem(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getColor(0, Color.WHITE), ctx.getColor(1, Color.RED));
	}

	@Override
	public String getConfig() {
		//{BeatColorSystem : [WHITE, GREEN]}
		return String.format("{%1s : [%2s, %3s]}", getName(), parent.format(primary, true), parent.format(pulse, true));
	}

	public Color getCurrentColor() {
		boolean isPulse =  parent.getAudio().getBeatInfo().getPulseDetector().isOnset();
		return isPulse ? pulse : primary;
	}
}
