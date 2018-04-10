package com.arranger.apv.test;

import org.junit.jupiter.api.Test;

import com.arranger.apv.util.Oscillator;

public class OscillatorTest extends APVPluginTest {


	@Test
	void testOscillator() throws Exception {
		Oscillator o = new Oscillator(parent);
		o.setTargetFrameRate(15);
		
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		for (int index = 0; index < 100; index++) {
			float result = o.oscillate(0, 1, 2);
			
			debug("result: " + result);
			
			min = Math.min(min, result);
			max = Math.max(max, result);
			advanceFrame();
		}
		
		assert(min == 0.0f);
		assert(max == 1.0f);
		
		//throw new Exception("testOscillator needs to be implemented");
	}

	@Override
	protected void setFrameIndexes() {
		this.frameIndexStart = 1;
		this.frameIndexEnd = 200;
	}
	
}
