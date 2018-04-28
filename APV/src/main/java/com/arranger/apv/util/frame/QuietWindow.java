package com.arranger.apv.util.frame;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class QuietWindow extends APVPlugin {
	
	protected FrameFader frameFader;

	public QuietWindow(Main parent, int framesToSkip) {
		super(parent);
		reset(framesToSkip);
	}

	public void reset(int framesToSkip) {
		frameFader = new FrameFader(parent, framesToSkip);
	}
	
	public boolean isInQuietWindow() {
		if (frameFader != null) {
			if (frameFader.isFadeNew() || frameFader.isFadeActive()) {
				return true;
			} else {
				frameFader = null;
				return false;
			}
		}
		return false;
	}
}
