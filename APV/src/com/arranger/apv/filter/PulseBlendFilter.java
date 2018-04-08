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
	
			private int mode;
			
			MODES(int mode) {
				this.mode = mode;
			}
	};

	private static final MODES DEFAULT_BLEND_MODE = MODES.BLEND;
	private static final int FADE_OUT_FRAMES = 40;
	
	MODES blendMode = DEFAULT_BLEND_MODE;
	int lastFrameCount = 0;
	
	public PulseBlendFilter(Main parent) {
		super(parent);
	}
	
	@Override
	public void preRender() {
		super.preRender();
		
		if (pulseDetector.isOnset()) {
			lastFrameCount = parent.getFrameCount();
		}
	
		if (parent.getFrameCount() - lastFrameCount < FADE_OUT_FRAMES) {
			doBlend();
		} else {
			parent.colorMode(DEFAULT_BLEND_MODE.mode);
			blendMode = DEFAULT_BLEND_MODE;
		}
		
		parent.addDebugMsg("  --blendMode: " + blendMode);
	}

	/**
	 * the greater the currentFrame is to the FADE_OUT_FRAMES
	 * the more opaque the effect should be
	 */
	private void doBlend() {
		if (blendMode == DEFAULT_BLEND_MODE) {
			int index = (int)parent.random(MODES.values().length - 1);
			blendMode = MODES.values()[index];
			parent.colorMode(blendMode.mode);
		}
		
		int currentFrame = (parent.getFrameCount() - lastFrameCount) % FADE_OUT_FRAMES;
		float alpha = PApplet.map(currentFrame, 0, FADE_OUT_FRAMES, 0, 255);
		
		int color = Color.BLACK.getRGB();
		parent.tint(color, alpha);
		parent.background(color, alpha);
		
		parent.addDebugMsg("  --alpha: " + alpha);
	}
}
