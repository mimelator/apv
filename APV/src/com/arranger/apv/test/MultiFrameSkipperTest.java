package com.arranger.apv.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.arranger.apv.util.MultiFrameSkipper;

public class MultiFrameSkipperTest extends APVPluginTest {
	private static final int FRAMES_TO_SKIP = 3;

	/**
	 * The multi frame skipper will only answer true to isNewFrame#every few frames
	 */
	private MultiFrameSkipper multiFrameSkipper;

	@BeforeEach
	public void beforeEach() {
		super.beforeEach();
		System.out.println("MultiFrameSkipper#beforeEach");
		multiFrameSkipper = new MultiFrameSkipper(parent, FRAMES_TO_SKIP);
	}

	@Test
	public void testMultiFrameSkipperTest() throws Exception {
		System.out.println("testMultiFrameSkipperTest");
		assert(multiFrameSkipper != null);
		
		boolean foundNewFrame = false;
		for (int index = 0; index < 20 - 1; index++) {
			System.out.println("Skipping frame: " + parent.getFrameCount());
			advanceFrame();
			if (multiFrameSkipper.isNewFrame()) {
				foundNewFrame = true;
				break;
			}
		}
		assert(foundNewFrame);
	}

	@Override
	protected void setFrameIndexes() {
		this.frameIndexStart = 1;
		this.frameIndexEnd = 5;
	}
}