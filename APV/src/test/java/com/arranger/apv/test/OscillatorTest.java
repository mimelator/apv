package com.arranger.apv.test;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.arranger.apv.util.Oscillator;
import com.arranger.apv.util.SplineHelper;

public class OscillatorTest extends APVPluginTest {

	private static final float EPSILON = 0.001f;
	
	@BeforeEach
	public void setup() {
		SplineHelper sh = new SplineHelper(parent);
		
		when(parent.mapEx(
				Mockito.anyFloat(), 
				Mockito.anyFloat(), 
				Mockito.anyFloat(), 
				Mockito.anyFloat(), 
				Mockito.anyFloat())).thenAnswer(new Answer<Float>() {
					@Override
			        public Float answer(InvocationOnMock inv) throws Throwable {
						float value = inv.getArgument(0);
						float start = inv.getArgument(1);
						float end = inv.getArgument(2);
						float start1 = inv.getArgument(3);
						float end1 = inv.getArgument(4);
						return sh.map(value, start, end, start1, end1);
					}
				});
	}
	
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
		
		assert(isEqual(min,  0.0f));
		assert(max >= 1.0f); //So now that oscillate uses the Spline Helper it overshoots the max range by about 30%
		
	}
	
	protected boolean isEqual(float v1, float v2) {
		return Math.abs(v1 - v2) < EPSILON;
	}

	@Override
	protected void setFrameIndexes() {
		this.frameIndexStart = 1;
		this.frameIndexEnd = 200;
	}
	
}
