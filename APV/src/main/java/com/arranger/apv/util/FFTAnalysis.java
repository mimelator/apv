package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import ddf.minim.analysis.FFT;

public class FFTAnalysis extends APVPlugin {

	public FFTAnalysis(Main parent) {
		super(parent);
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
