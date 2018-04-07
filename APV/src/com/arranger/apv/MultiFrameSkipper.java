package com.arranger.apv;

public class MultiFrameSkipper extends SingleFrameSkipper {

	protected int framesToSkip;
	
	public MultiFrameSkipper(Main parent, int framesToSkip) {
		super(parent);
		this.framesToSkip = framesToSkip;
	}

	@Override
	public boolean isNewFrame() {
		return super.isNewFrame() && (parent.frameCount % framesToSkip == 0);
	}
}
