package com.arranger.apv.color;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.frame.FrameFader;
import com.arranger.apv.util.frame.SingleFrameSkipper;

/**
 * Basic BeatColor System toggles between red & white
 */
public class BeatColorSystem extends ColorSystem {
	
	private static final int FRAMES_TO_FADE_COLOR = 20;
	
	private Color primary, pulse;
	
	private FrameFader fader;
	protected SingleFrameSkipper skipper;
	protected Color lastColor;
	
	public BeatColorSystem(Main parent) {
		this(parent, Color.WHITE, Color.RED);
	}
	
	public BeatColorSystem(Main parent, Color primary, Color pulse) {
		super(parent);
		this.primary = primary;
		this.pulse = pulse;
		fader = new FrameFader(parent, FRAMES_TO_FADE_COLOR);
		skipper = new SingleFrameSkipper(parent);
	}
	
	public BeatColorSystem(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getColor(0, Color.WHITE), ctx.getColor(1, Color.RED));
	}

	@Override
	public String getConfig() {
		//{BeatColorSystem : [WHITE, GREEN]}
		return String.format("{%s : [%s, %s]}", getName(), parent.format(primary, true), parent.format(pulse, true));
	}
	
	public Color getCurrentColor() {
		if (lastColor != null && !skipper.isNewFrame()) {
			return lastColor;
		}
		
		boolean isPulse =  parent.getAudio().getBeatInfo().getPulseDetector().isOnset();
		if (isPulse) {
			fader.startFade();
		}
		
		Color result;
		if (!fader.isFadeActive()) {
			result = primary;
		} else {
			float fadePct = fader.getFadePct();
			int lerpColor = parent.lerpColor(primary.getRGB(), pulse.getRGB(), fadePct);
			result = new Color(lerpColor);
		}
		
		lastColor = result;
		return result;
	}
}
