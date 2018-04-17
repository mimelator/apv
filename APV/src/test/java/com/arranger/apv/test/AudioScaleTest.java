package com.arranger.apv.test;

import org.junit.jupiter.api.Test;

import com.arranger.apv.util.APVFloatScalar;

public class AudioScaleTest extends APVPluginTest {

	private static final int ARRAY_SIZE = 5;
	private static final float EPSILON = 0.001f;


	public AudioScaleTest() {
	}
	
	@Test
	public void testScalar() {
		APVFloatScalar fs = new APVFloatScalar(parent);
		
		float[] values = createFloatArray(ARRAY_SIZE, 1);
		fs.scale(values, .5f);
		
		assert(values.length == ARRAY_SIZE);
		checkArraySamples(values, .5f);
		
		values = createFloatArray(ARRAY_SIZE, 1);
		fs.scale(values, -1f);
		checkArraySamples(values, .1f);
		
		values = createFloatArray(ARRAY_SIZE, 1);
		fs.scale(values, -2f);
		checkArraySamples(values, .01f);
		
		values = createFloatArray(ARRAY_SIZE, 10);
		fs.scale(values, 1);
		checkArraySamples(values, 10f);
		
		values = createFloatArray(ARRAY_SIZE, 10);
		fs.scale(values, -1);
		checkArraySamples(values, 1f);
		
		values = createFloatArray(ARRAY_SIZE, 10);
		fs.scale(values, -2);
		checkArraySamples(values, .1f);
	}
	
	protected void checkArraySamples(float [] values, float targetVal) {
		for (int index = 0; index < values.length; index++) {
			assert(isEqual(values[index], targetVal));
		}
	}

	protected float[] createFloatArray(int size, float value) {
		float [] values = new float[size];
		for (int index = 0; index < values.length; index++) {
			values[index] = value;
		}
		return values;
	}
	
	protected boolean isEqual(float v1, float v2) {
		return Math.abs(v1 - v2) < EPSILON;
	}

	@Override
	protected void setFrameIndexes() {

	}

}
