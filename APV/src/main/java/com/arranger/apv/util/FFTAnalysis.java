package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import ddf.minim.analysis.FFT;

public class FFTAnalysis extends APVPlugin {

	public FFTAnalysis(Main parent) {
		super(parent);
	}

	boolean invert = false;
	
	public float getMappedAmpInv(float start, float end, float start1, float end1) {
		float mappedAmp = getMappedAmp(start, end, start1, end1);
		if (invert) {
			mappedAmp = -mappedAmp;
		}
		invert = !invert;
		return mappedAmp;
	}
	
	
	public float getMappedAmp(float start, float end, float start1, float end1) {
		SplineInterpolator si = new SplineInterpolator(start, end, start1, end1);
		return (float)si.interpolate(getMaxAmp());
	}
	
	public float getMaxAmp() {
		FFT fft = parent.getAudio().getBeatInfo().getFFT();
		float bounds = fft.avgSize();
		float maxAmp = Float.MIN_VALUE;
		for (int index = 0; index < bounds; index++) {
			float amplitude = fft.getAvg(index);
			maxAmp = Math.max(maxAmp, amplitude);
		}
		return maxAmp;
	}
}
