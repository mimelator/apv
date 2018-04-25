package com.arranger.apv.test;

import org.junit.jupiter.api.Test;

import com.arranger.apv.util.APVTrig;

public class TrigTest extends APVPluginTest {

	private static final float EPSILON = .01f;
	
	public TrigTest() {
	}

	@Test
	public void testAccuracy() {
		APVTrig trig = new APVTrig(parent);
		
		for (float index = 0; index < 700; index += .1f) {
			double val1 = trig.atan(index);
			double val2 = Math.atan(Math.toRadians(index));
			
			double difference = Math.abs(val2 - val1);
			if (difference > EPSILON) {
				System.out.printf("dif at index: %f of %f\n", index, difference);
				assert(false);
			}
		}
	}
	
	
	@Override
	protected void setFrameIndexes() {

	}

}
