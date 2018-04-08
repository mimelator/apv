package com.arranger.apv;

import java.util.logging.Logger;

import com.arranger.apv.util.FrameFader;

import processing.core.PImage;

/**
 * Right now, i think this class is supposed to:
 * 		Pay attention to when a new transition starts
 * 		Capture that image and then start blending it to the subsequent frames
 * 		
 *		However, we need a signal from the Main to start a transition.
 * 	{@link #startTransition()} can be called as many times as needed.
 *
 */
public abstract class TransitionSystem extends ShapeSystem {
	
	private static final Logger logger = Logger.getLogger(TransitionSystem.class.getName());
	
	private static final int DEFAULT_FRAMES_TO_FADE = 20;//30;
	
	protected FrameFader frameFader;
	protected PImage savedImage;
	protected int savedImageFrame;
	
	public TransitionSystem(Main parent) {
		super(parent, null);
		frameFader = new FrameFader(parent, DEFAULT_FRAMES_TO_FADE);
	}
	
	public abstract void doTransition(float pct);
	
	public void onDrawStart() {
		//Do nothing
	}
	
	@Override
	public void setup() {
		
	}

	@Override
	public void draw() {
		onDrawStop();
	}

	public void onDrawStop() {
		logger.fine("frame: " + parent.getFrameCount());
		if (frameFader.isFadeNew()) {
			logger.info("newFade at frame: " + parent.getFrameCount());
			captureCurrentImage();
		} else {
			if (frameFader.isFadeActive() && savedImage != null) {
				//fade it out
				float fadePct = frameFader.getFadePct();
				doTransition(fadePct);
				logger.fine("fadePct: " + fadePct);
			} else {
				savedImage = null;
			}
		}
	}

	public void captureCurrentImage() {
		savedImage = parent.get();
		savedImageFrame = parent.getFrameCount();
	}
	
	public void startTransition() {
		frameFader.startFade();
		captureCurrentImage();
	}
}
