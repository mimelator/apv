package com.arranger.apv.test;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.arranger.apv.util.FFTAnalysis;

import ddf.minim.analysis.FFT;

public class FFTAnalysisTest extends APVPluginTest {

	public FFTAnalysisTest() {
	}

	@BeforeEach
	protected void prepareFFT() {
		FFT fft = parent.getAudio().getBeatInfo().getFFT();
		when(fft.avgSize()).thenReturn(5);
		when(fft.getAvg(Mockito.anyInt())).thenReturn(2.0f);
	}
	
	@Test
	public void testFFTMapping() {
		FFTAnalysis fa = new FFTAnalysis(parent);
		assert(2.0f == fa.getMaxAmp());
		
		float result = fa.getMappedAmp(0, 2, 1, 255);
		int i = Math.round(result);
		assert(i == 255);
	}
	
	
	@Override
	protected void setFrameIndexes() {
	}

}
