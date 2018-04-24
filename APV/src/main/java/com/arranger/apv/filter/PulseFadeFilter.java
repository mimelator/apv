package com.arranger.apv.filter;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.frame.PulseFader;

public class PulseFadeFilter extends Filter {

	private static final int FADE_OUT_FRAMES = 40;
	
	
	protected Color color = Color.BLACK;
	protected float alpha = 0;
	protected PulseFader pulseFader;
	
	public PulseFadeFilter(Main parent) {
		super(parent);
	}
	
	public PulseFadeFilter(Main parent, Color color, int pulsesToSkip) {
		super(parent);
		this.color = color;
		
		pulseFader = new PulseFader(parent, FADE_OUT_FRAMES, Main.MAX_ALPHA, 0, pulsesToSkip);
	}
	
	public PulseFadeFilter(Configurator.Context ctx) {
		this(ctx.getParent(),
				ctx.getColor(0, Color.BLACK),
				ctx.getInt(1, PulseListener.DEFAULT_PULSES_TO_SKIP));
	}
	
	@Override
	public String getConfig() {
		//	{PulseFadeFilter : [RED, 1]}
		return String.format("{%s : [%s, %d]}", 
				getName(), 
				parent.format(color, true), 
				pulseFader.getPulsesToSkip());
	}

	@Override
	public void preRender() {
		super.preRender();
		
		alpha = pulseFader.getValue();
		if (alpha > 0) {
			doFade();
		}
		
		addSettingsMsg();
	}
	
	protected void addSettingsMsg() {
		parent.addSettingsMessage("  --color: " + parent.format(color));
		parent.addSettingsMessage("  --alpha: " + alpha);
	}

	/**
	 * the greater the currentFrame is to the FADE_OUT_FRAMES
	 * the more opaque the effect should be
	 */
	protected void doFade() {
		parent.fill(color.getRGB(), alpha);
		parent.rect(0, 0, parent.width, parent.height);  
	}
}
