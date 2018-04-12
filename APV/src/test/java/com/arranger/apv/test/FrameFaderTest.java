package com.arranger.apv.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.arranger.apv.util.FrameFader;

public class FrameFaderTest extends APVPluginTest {
	
	private static final int FRAMES_TO_FADE = 10;

	private FrameFader frameFader;

	@BeforeEach
	public void beforeEach() {
		super.beforeEach();
		debug("FrameFaderTest#beforeEach");
		frameFader = new FrameFader(parent, FRAMES_TO_FADE);
	}

	@Test
	public void testFrameFader() throws Exception {
		debug("testFrameFader");
		assert(frameFader != null);
		frameFader.startFade();
		
		assert(frameFader.isFadeActive());
		assert(frameFader.isFadeNew()); 
		
		for (int index = 0; index < FRAMES_TO_FADE - 1; index++) {
			advanceFrame();
			
			assert(frameFader.isFadeActive());
			float fadePct = frameFader.getFadePct();
			debug("index: " + index + " frameCount: " + parent.getFrameCount() + " fadePct: " + fadePct);
		}
		
		//bump to the last frame
		advanceFrame();
		assert(!frameFader.isFadeActive());
	}



	@Override
	protected void setFrameIndexes() {
		this.frameIndexStart = 0;
		this.frameIndexEnd = 10;
	}
}
