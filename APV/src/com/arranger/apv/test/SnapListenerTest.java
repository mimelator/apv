package com.arranger.apv.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.arranger.apv.audio.SnapListener;

public class SnapListenerTest extends APVPluginTest {
	
	private static final int FRAMES_TO_SKIP = 5;
	
	private SnapListener snapListener;
	
	@BeforeEach
	public void beforeEach() {
		super.beforeEach();
		debug("SnapListenerTest#beforeEach");
		snapListener = new SnapListener(parent, FRAMES_TO_SKIP);
	}
	
	@Test
	public void testSnapListenerTest() throws Exception {
		debug("testSnapListenerTest");
		assert(snapListener != null);
		
		int snapsFound = 0;
		for (int index = 0; index < 30; index++) {
			advanceFrame();
			if (snapListener.isSnap()) {
				snapsFound++;
				debug("Snaps found: " + snapsFound);
				
				//This should still return true this frame
				boolean again = snapListener.isSnap();
				assert(again);
				
				//advance some frames
				advanceFrame();
				advanceFrame();
				
				//should not immediately register as another snap
				// due to the "Quiet Window" of the FramesToSkip
				again = snapListener.isSnap();
				assert(!again);
			} else {
				//this should continue to return false during this frame
				boolean again = snapListener.isSnap();
				assert(!again);
			}
		}
		assert(snapsFound > 4);
		
	}
	
	@Override
	protected void setFrameIndexes() {
		this.frameIndexStart = 1;
		this.frameIndexEnd = 100;
		
	}

}
