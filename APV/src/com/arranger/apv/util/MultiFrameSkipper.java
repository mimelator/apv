package com.arranger.apv.util;

import com.arranger.apv.Main;

public class MultiFrameSkipper extends SingleFrameSkipper {

	protected int framesToSkip;
	
	public MultiFrameSkipper(Main parent, int framesToSkip) {
		super(parent);
		this.framesToSkip = framesToSkip;
	}

	@Override
	public boolean isNewFrame() {
		return super.isNewFrame() && (parent.getFrameCount() % framesToSkip == 0);
	}
}
