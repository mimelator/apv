package com.arranger.apv.scene;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator.Context;

public abstract class Animation extends Scene {

	private static final int FRAMES_REQUIRED_TO_RESET = 60;
	
	public Animation(Main parent) {
		super(parent);
	}

	public Animation(Context ctx) {
		super(ctx);
	}

	public Animation(Scene o) {
		super(o);
	}
	
	@Override
	public void setup() {
	}

	@Override
	public boolean isNew() {
		boolean wasReset = false;
		int currentFrame = parent.getFrameCount();
		if (currentFrame > lastFrameDrawn + FRAMES_REQUIRED_TO_RESET) {
			reset();
			wasReset = true;
		}
		return wasReset;
	}
	
	protected abstract void reset();
}
