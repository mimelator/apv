package com.arranger.apv.archive;

import com.arranger.apv.Main;
import com.arranger.apv.color.BeatColorSystem;
import com.arranger.apv.color.OscillatingColor;
import com.arranger.apv.filter.PulseBasedFilter;

import processing.core.PApplet;

/**
 * https://processing.org/tutorials/pixels/
 * 
 * https://processing.org/reference/blendMode_.html
 * 
 * BLEND - linear interpolation of colours: C = A*factor + B. This is the default blending mode.

	ADD - additive blending with white clip: C = min(A*factor + B, 255)
	
	SUBTRACT - subtractive blending with black clip: C = max(B - A*factor, 0)
	
	DARKEST - only the darkest colour succeeds: C = min(A*factor, B)
	
	LIGHTEST - only the lightest colour succeeds: C = max(A*factor, B)
	
	DIFFERENCE - subtract colors from underlying image.
	
	EXCLUSION - similar to DIFFERENCE, but less extreme.
	
	MULTIPLY - multiply the colors, result will always be darker.
	
	SCREEN - opposite multiply, uses inverse values of the colors.
	
	REPLACE - the pixels entirely replace the others and don't utilize alpha (transparency) values
 */
public class BeatTintFilter extends PulseBasedFilter {

	private static final int FADE_OUT_FRAMES = 50; 
	int lastFrameCount = 0;
	int blendMode;
	BeatColorSystem colorSystem;
	
	
	public BeatTintFilter(Main parent, int blendMode) {
		super(parent);
		colorSystem = new OscillatingColor(parent);
		this.blendMode = blendMode;
	}

	@Override
	public void preRender() {
		super.preRender();
		
		if (pulseDetector.isOnset()) {
			lastFrameCount = parent.getFrameCount();
		}
	
		if (parent.getFrameCount() - lastFrameCount < FADE_OUT_FRAMES) {
			doTint();
		}
	}
	
	private void doTint() {
		parent.colorMode(blendMode);
		//I want to fade in and fade out the tint
		int currentFrame = (parent.getFrameCount() - lastFrameCount) % FADE_OUT_FRAMES;
		
		//the greater the currentFrame is to the FADE_OUT_FRAMES
		//the more opaque the effect should be
		
		float alpha = PApplet.map(currentFrame, 0, FADE_OUT_FRAMES, 0, 50);
		int color = colorSystem.getCurrentColor().getRGB();
		parent.tint(color, alpha);
		parent.background(color, alpha);
		
		parent.addSettingsMessage("  --blendMode: " + blendMode);
		parent.addSettingsMessage("  --color: " + color);
		parent.addSettingsMessage("  --alpha: " + alpha);
	}
}
