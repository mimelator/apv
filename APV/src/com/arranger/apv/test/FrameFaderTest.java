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
		System.out.println("FrameFaderTest#beforeEach");
		frameFader = new FrameFader(parent, FRAMES_TO_FADE);
	}

	@Test
	public void testFrameFader() throws Exception {
		System.out.println("testFrameFader");
		assert(frameFader != null);
		frameFader.startFade();
		
		assert(frameFader.isFadeActive());
		
		for (int index = 0; index < FRAMES_TO_FADE - 1; index++) {
			//bump to the next frame
			frameIterator.next();
			
			assert(frameFader.isFadeActive());
			float fadePct = frameFader.getFadePct();
			System.out.println("index: " + index + " frameCount: " + parent.getFrameCount() + " fadePct: " + fadePct);
		}
		
		//bump to the last frame
		frameIterator.next();
		assert(!frameFader.isFadeActive());
	}

	@Override
	protected void setFrameIndexes() {
		this.frameIndexStart = 0;
		this.frameIndexEnd = 10;
	}
}
