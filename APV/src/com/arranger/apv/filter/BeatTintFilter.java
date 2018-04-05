package com.arranger.apv.filter;

import com.arranger.apv.BeatColorSystem;
import com.arranger.apv.BeatColorSystem.OscillatingColor;
import com.arranger.apv.Main;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * https://processing.org/tutorials/pixels/
 */
public class BeatTintFilter extends BeatFilter implements PConstants {

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
		
		if (beatDetect.isKick()) {
			lastFrameCount = parent.frameCount;
		}
	
		if (parent.frameCount - lastFrameCount < FADE_OUT_FRAMES) {
			doTint();
		}
	}
	
	private void doTint() {
		parent.colorMode(blendMode);
		//I want to fade in and fade out the tint
		int currentFrame = (parent.frameCount - lastFrameCount) % FADE_OUT_FRAMES;
		
		//the greater the currentFrame is to the FADE_OUT_FRAMES
		//the more opaque the effect should be
		
		float alpha = PApplet.map(currentFrame, 0, FADE_OUT_FRAMES, 0, 50);
		int color = colorSystem.getCurrentColor().getRGB();
		parent.tint(color, alpha);
		parent.background(color, alpha);
		
		parent.addDebugMsg("  --blendMode: " + blendMode);
		parent.addDebugMsg("  --color: " + color);
		parent.addDebugMsg("  --alpha: " + alpha);
	}
}
