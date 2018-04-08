package com.arranger.apv;

import com.arranger.apv.util.MultiFrameSkipper;

import processing.core.PImage;

public class TransitionSystem extends APVPlugin {

	private static final int DEFAULT_FRAMES_TO_FADE = 10;
	
	protected MultiFrameSkipper frameSkipper;
	protected PImage pImage;
	
	public TransitionSystem(Main parent) {
		super(parent);
		frameSkipper = new MultiFrameSkipper(parent, DEFAULT_FRAMES_TO_FADE);
	}
	
	public void onDrawStart() {
		if (pImage != null) {
			drawImage();
		}
	}
	
	
	public void onDrawStop() {
		if (pImage == null) {
			pImage = parent.get();
		}
	}
	
	protected boolean transitionComplete() {
		return false;
	}
	
	

	/**
	 * 
	 */
	protected void drawImage() {
		
	}
}
