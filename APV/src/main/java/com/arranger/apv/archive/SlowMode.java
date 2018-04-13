package com.arranger.apv.archive;

import com.arranger.apv.Main;
import com.arranger.apv.Switch;
import com.arranger.apv.transition.Fade;
import com.arranger.apv.util.Configurator.Context;

import processing.core.PApplet;

public class SlowMode extends Fade {

	public SlowMode(Main parent, int fadesToTransition) {
		super(parent, fadesToTransition);
	}

	public SlowMode(Context ctx) {
		super(ctx);
	}

	@Override
	public void startTransition() {
		super.startTransition();
		Switch s = parent.getSwitch("FrameStrober");
		if (!s.isEnabled()) {
			s.toggleEnabled();
		}
	}

	@Override
	protected void onTransitionComplete() {
		super.onTransitionComplete();
		Switch s = parent.getSwitch("FrameStrober");
		if (s.isEnabled()) {
			s.toggleEnabled();
		}
	}

	@Override
	public void doTransition(float pct) {
		super.doTransition(pct);
		
		//i want to scale the frameStrober SkipNFrames as the
		// pct goes from 1 down to 0
		int frameStrobeRate = (int)PApplet.map(pct, 1, 0, 5, 30);
		parent.getFrameStrober().setSkipNFrames(frameStrobeRate);
	}
}
