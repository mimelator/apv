package com.arranger.apv.audio;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import ddf.minim.AudioListener;
import ddf.minim.AudioPlayer;
import ddf.minim.AudioSource;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;

public class BeatInfo extends APVPlugin {

	protected AudioSource source;
	protected BeatDetect pulseDetector;
	protected BeatDetect freqDetector;
	protected FFT fft;
	protected AudioListener listener;
	
	public BeatInfo(Main parent, AudioSource source) {
		super(parent);
		setup(source);
	}

	protected void setup(AudioSource source) {
		this.source = source;
		
		pulseDetector = new BeatDetect() {
			@Override
			public boolean isOnset() {
				if (parent.getPulseListener().isArtificialPulse()) {
					return true;
				} else {
					return super.isOnset();
				}
			}
			
			
		};
		int sensitivity = parent.getConfigInt(Main.FLAGS.PULSE_SENSITIVITY.apvName());
		pulseDetector.setSensitivity(sensitivity);
		
		freqDetector = new BeatDetect(source.bufferSize(), source.sampleRate());
		freqDetector.setSensitivity(5);
		createFFT();
		createListener();
		source.addListener(listener);
	}
	
	public void updateSource(AudioSource newSource) {
		if (!parent.getAudio().isLineIn() && source != null) {
			source.removeListener(listener);
		}
		
		if (source instanceof AudioPlayer) {
			((AudioPlayer)source).close();
		}
			
		setup(newSource);
	}
	
	protected void createFFT() {
		fft = new FFT(source.bufferSize(), source.sampleRate());
		fft.logAverages(10, 7); //Hard coded constants
	}
	
	public AudioSource getSource() {
		return source;
	}
	
	public FFT getFFT() {
		return fft;
	}
	
	public BeatDetect getPulseDetector() {
		return pulseDetector;
	}

	public BeatDetect getFreqDetector() {
		return freqDetector;
	}
	
	private void createListener() {
		this.listener =  new AudioListener() {
			public void samples(float[] samps) {
				Audio.scale(samps, parent.getAudio().getDB());
				pulseDetector.detect(samps);
				freqDetector.detect(samps);
				fft.forward(samps);
			}

			public void samples(float[] sampsL, float[] sampsR) {
				freqDetector.detect(sampsL); 
				pulseDetector.detect(sampsL); 
				fft.forward(sampsL);
			}
		};
	}
}