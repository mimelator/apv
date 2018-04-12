package com.arranger.apv;

import java.util.logging.Logger;

import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FrameFader;

import processing.core.PApplet;
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
	
	private static final int INITIAL_ALPHA_FOR_FADE = 90;
	
	private static final Logger logger = Logger.getLogger(TransitionSystem.class.getName());
	
	protected FrameFader frameFader;
	protected PImage savedImage;
	protected int savedImageFrame;
	
	public TransitionSystem(Main parent, int fadesToTransition) {
		super(parent, null);
		frameFader = new FrameFader(parent, fadesToTransition);
	}
	
	public TransitionSystem(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, Main.DEFAULT_TRANSITION_FRAMES));
	}
	
	/**
	 * Pct will decrease from 1.0f to 0 
	 * @see FrameFader#getFadePct()
	 */
	public abstract void doTransition(float pct);
	
	public void onDrawStart() {
		//Do nothing
	}
	
	public void incrementTransitionFrames() {
		int numFramesToFade = frameFader.getNumFramesToFade();
		frameFader.setNumFramesToFade(numFramesToFade++);
	}
	
	public void decrementTransitionFrames() {
		int numFramesToFade = frameFader.getNumFramesToFade();
		frameFader.setNumFramesToFade(numFramesToFade--);
	}
	
	public int getTransitionFrames() {
		return frameFader.getNumFramesToFade();
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
	
	protected float getAlapha(float pct) {
		float alpha = PApplet.lerp(0, INITIAL_ALPHA_FOR_FADE, pct);
		return alpha;
	}
	
	protected void doStandardFade(float pct) {
		float alpha = getAlapha(pct);
		parent.tint(255, alpha);  // Display at half opacity
	}
}