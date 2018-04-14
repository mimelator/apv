package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import processing.core.PImage;

public class SavedImage extends APVPlugin {
	private PImage savedImage;
	private int savedImageFrame;

	public SavedImage(Main parent) {
		super(parent);
		setSavedImage(parent.get());
		setSavedImageFrame(parent.getFrameCount());
	}

	public PImage getSavedImage() {
		return savedImage;
	}

	public void setSavedImage(PImage savedImage) {
		this.savedImage = savedImage;
	}

	public int getSavedImageFrame() {
		return savedImageFrame;
	}

	public void setSavedImageFrame(int savedImageFrame) {
		this.savedImageFrame = savedImageFrame;
	}
}