package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class APVFloatScalar extends APVPlugin {

	public APVFloatScalar(Main parent) {
		super(parent);
	}
	
	/**
	 * Scales in place  (not very functional)
	 * When the sf goes below Zero, the following happens: 1 x 10^sf
	 * 
	 * So, -3 becomes 10^-3 or .0001
	 */
	public void scale(float [] samps, float sf) {
		if (sf < 0) {
			sf = (float)Math.pow(10, sf);
		}
		
		for (int i=0; i<samps.length; i++) {
			samps[i] *= sf;
		}
	}
	
}
