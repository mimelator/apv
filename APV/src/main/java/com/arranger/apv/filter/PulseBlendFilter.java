package com.arranger.apv.filter;

import java.awt.Color;

import com.arranger.apv.Main;

import processing.core.PApplet;

/**
 * https://processing.org/reference/blendMode_.html
	BLEND - linear interpolation of colours: C = A*factor + B. This is the default blending mode.
	ADD - additive blending with white clip: C = min(A*factor + B, 255)
	SUBTRACT - subtractive blending with black clip: C = max(B - A*factor, 0)
	DARKEST - only the darkest colour succeeds: C = min(A*factor, B)
	LIGHTEST - only the lightest colour succeeds: C = max(A*factor, B)
	DIFFERENCE - subtract colors from underlying image.
	EXCLUSION - similar to DIFFERENCE, but less extreme.
	MULTIPLY - multiply the colors, result will always be darker.
	SCREEN - opposite multiply, uses inverse values of the colors.
	REPLACE - the pixels entirely replace the others and don't utilize alpha (transparency) values
 * 
 * 
 */
public class PulseBlendFilter extends PulseBasedFilter {
	
	enum MODES {
			BLEND(PApplet.BLEND),
			ADD(PApplet.BLEND),
			SUBTRACT(PApplet.SUBTRACT),
			DARKEST(PApplet.DARKEST),
			LIGHTEST(PApplet.LIGHTEST),
			DIFFERENCE(PApplet.DIFFERENCE),
			EXCLUSION(PApplet.EXCLUSION),
			MULTIPLY(PApplet.MULTIPLY),
			SCREEN(PApplet.SCREEN),
			REPLACE(PApplet.REPLACE);
	
			protected int mode;
			
			MODES(int mode) {
				this.mode = mode;
			}
	};

	protected static final MODES DEFAULT_BLEND_MODE = MODES.BLEND;
	private static final int FADE_OUT_FRAMES = 40;
	
	protected int backgroundColor = Color.BLACK.getRGB();
	protected MODES blendMode = DEFAULT_BLEND_MODE;
	protected int lastFrameCount = 0;
	
	public PulseBlendFilter(Main parent) {
		super(parent);
	}
	
	@Override
	public void preRender() {
		super.preRender();
		
		if (pulseDetector.isOnset()) {
			onNewPulse();
		}
	
		int fof = getFadeOutFrames();
		if (parent.getFrameCount() - lastFrameCount < fof) {
			doBlend();
		} else {
			parent.colorMode(DEFAULT_BLEND_MODE.mode);
			blendMode = DEFAULT_BLEND_MODE;
		}
		
		addSettingsMsg();
	}

	protected int getFadeOutFrames() {
		return FADE_OUT_FRAMES;
	}
	
	protected void onNewPulse() {
		lastFrameCount = parent.getFrameCount();
	}
	
	protected void addSettingsMsg() {
		parent.addSettingsMessage("  --blendMode: " + blendMode);
	}

	/**
	 * the greater the currentFrame is to the FADE_OUT_FRAMES
	 * the more opaque the effect should be
	 */
	protected void doBlend() {
		if (blendMode == DEFAULT_BLEND_MODE) {
			int index = (int)parent.random(MODES.values().length - 1);
			blendMode = MODES.values()[index];
			parent.colorMode(blendMode.mode);
		}
		
		int currentFrame = (parent.getFrameCount() - lastFrameCount) % getFadeOutFrames();
		float alpha = PApplet.map(currentFrame, 0, getFadeOutFrames(), 0, Main.MAX_ALPHA);
		
		parent.tint(backgroundColor, alpha);
		parent.background(backgroundColor, alpha);
		
		parent.addSettingsMessage("  --alpha: " + alpha);
	}
}
