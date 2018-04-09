package com.arranger.apv.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.arranger.apv.audio.PulseListener;

public class PulseListenerTest extends APVPluginTest {
	
	private static final int FRAMES_TO_SKIP = 5;
	
	private PulseListener pulseListener;
	
	@BeforeEach
	public void beforeEach() {
		super.beforeEach();
		debug("PulseListenerTest#beforeEach");
		pulseListener = new PulseListener(parent, FRAMES_TO_SKIP);
	}

	
	@Test
	public void pulseListenerTest() throws Exception {
		debug("pulseListenerTest");
		assert(pulseListener != null);
	
		//the Mocked pulseListener has a BeatDetect that always return true;

		boolean foundNewPulse = false;
		for (int index = 0; index < 17; index++) {
			advanceFrame();
			if (pulseListener.isNewPulse()) {
				debug("Found new Pulse");
				foundNewPulse = true;
			}
		}
		assert(foundNewPulse);
	}
	
	
	@Override
	protected void setFrameIndexes() {
		this.frameIndexStart = 1;
		this.frameIndexEnd = 40;
		
	}
}
