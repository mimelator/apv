package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class APVTrig extends APVPlugin {

	private static final int DEGREES = 720;
	private static final float PRECISION = .1f;
	
	public APVTrig(Main parent) {
		super(parent);
	}

	public double atan(double a) {
		return LOOKUP_TABLE[mapLookupToIndex(a)];
	}
	
	private static int mapLookupToIndex(double lookup) {
		lookup %= DEGREES;
		while (lookup < 0) {
			lookup += DEGREES;
		}
		return (int)(lookup / PRECISION);
	}
	
	private static final double [] LOOKUP_TABLE = new double[(int)(DEGREES / PRECISION)];
	static {
		for (float index = 0; index < DEGREES; index += PRECISION) {
			LOOKUP_TABLE[mapLookupToIndex(index)] = Math.atan(Math.toRadians(index));
		}
	}
	
}
