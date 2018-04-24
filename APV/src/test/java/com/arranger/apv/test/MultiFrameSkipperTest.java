package com.arranger.apv.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.arranger.apv.util.frame.MultiFrameSkipper;

public class MultiFrameSkipperTest extends APVPluginTest {
	private static final int FRAMES_TO_SKIP = 3;
	private static final int FRAMES_TO_SKIP_RESET = 7;

	/**
	 * The multi frame skipper will only answer true to isNewFrame#every few frames
	 */
	private MultiFrameSkipper multiFrameSkipper;

	@BeforeEach
	public void beforeEach() {
		super.beforeEach();
		debug("MultiFrameSkipper#beforeEach");
		multiFrameSkipper = new MultiFrameSkipper(parent, FRAMES_TO_SKIP);
	}

	@Test
	public void testMultiFrameSkipperTest() throws Exception {
		debug("testMultiFrameSkipperTest");
		assert(multiFrameSkipper != null);
		
		boolean foundNewFrame = false;
		for (int index = 0; index < 20; index++) {
			advanceFrame();
			if (multiFrameSkipper.isNewFrame()) {
				debug("Found new Frame");
				foundNewFrame = true;
				
				//This should only return true once / frame
				boolean again = multiFrameSkipper.isNewFrame();
				assert(!again);
			} else {
				//this should continue to return false during this frame
				boolean again = multiFrameSkipper.isNewFrame();
				assert(!again);
			}
		}
		assert(foundNewFrame);
		

		debug("Testing Reset");
		multiFrameSkipper.reset(FRAMES_TO_SKIP_RESET);
		foundNewFrame = false;
		for (int index = 0; index < 40; index++) {
			advanceFrame();
			if (multiFrameSkipper.isNewFrame()) {
				debug("Found new Frame");
				foundNewFrame = true;
				
				//This should only return true once / frame
				boolean again = multiFrameSkipper.isNewFrame();
				assert(!again);
			} else {
				//this should continue to return false during this frame
				boolean again = multiFrameSkipper.isNewFrame();
				assert(!again);
			}
		}
		assert(foundNewFrame);
	}

	@Override
	protected void setFrameIndexes() {
		this.frameIndexStart = 1;
		this.frameIndexEnd = 90;
	}
}