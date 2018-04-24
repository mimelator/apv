package com.arranger.apv.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.arranger.apv.util.frame.SingleFrameSkipper;


public class SingleFrameSkipperTest extends APVPluginTest {
	
	/**
	 * The SingleFrameSkipper only allows one action per frame by return yes or no for isNewFrame 
	 */
	private SingleFrameSkipper singleFrameSkipper;

	@BeforeEach
	public void beforeEach() {
		super.beforeEach();
		debug("SingleFrameSkipper#beforeEach");
		singleFrameSkipper = new SingleFrameSkipper(parent);
	}

	@Test
	public void testSingleFrameSkipper() throws Exception {
		debug("testSingleFrameSkipper");
		assert(singleFrameSkipper != null);
		assert(singleFrameSkipper.isNewFrame());
		
		advanceFrame();
		assert(singleFrameSkipper.isNewFrame());
		
		//2nd action on this frame
		assert(!singleFrameSkipper.isNewFrame());
		
		advanceFrame();
		assert(singleFrameSkipper.isNewFrame());
	}

	@Override
	protected void setFrameIndexes() {
		this.frameIndexStart = 0;
		this.frameIndexEnd = 10;
	}
}
