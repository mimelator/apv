package com.arranger.apv.audio;


import com.arranger.apv.APVPlugin;
import com.arranger.apv.CommandSystem;
import com.arranger.apv.Main;
import com.arranger.apv.util.APVFloatScalar;

import ddf.minim.AudioInput;
import ddf.minim.AudioListener;
import ddf.minim.AudioPlayer;
import ddf.minim.AudioSource;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;

/**
 * http://code.compartmental.net/tools/minim/
 */
public class Audio extends APVPlugin {
	
	private static final float DEFAULT_PULSE_DECTECT_SCALAR = 5.0f;
	
	protected APVFloatScalar floatScalar;
	protected BeatInfo beatInfo;
	public float scaleFactor = DEFAULT_PULSE_DECTECT_SCALAR;
	
	public Audio(Main parent, int bufferSize) {
		super(parent);
		floatScalar = new APVFloatScalar(parent);
		Minim minim = new Minim(parent);
		AudioSource source = minim.getLineIn(Minim.MONO, bufferSize);
		
		beatInfo = new BeatInfo(source);
		if (source instanceof AudioPlayer) {
			AudioPlayer audioPlayer = (AudioPlayer)source;
			audioPlayer.loop();
		} else if (source instanceof AudioInput) {
			AudioInput audioInput = (AudioInput)source;
			if (audioInput.isMonitoring()) {
				audioInput.disableMonitoring();
			}
		}
		
		parent.registerSetupListener(() -> {
					CommandSystem cs = parent.getCommandSystem();
				cs.registerCommand('+', "Audio", "Increases the audio sensitivity", event -> scaleFactor++);
				cs.registerCommand('-', "Audio", "Decreases the audio sensitivity", event -> scaleFactor--);
		});
	}
	
	public float getScaleFactor() {
		return scaleFactor;
	}

	public BeatInfo getBeatInfo() {
		return beatInfo;
	}
	
	public class BeatInfo {

		protected AudioSource source;
		protected BeatDetect pulseDetector;
		protected BeatDetect freqDetector;
		protected FFT fft;
		
		public BeatInfo(AudioSource source) {
			this.source = source;
			pulseDetector = new BeatDetect(); 
			pulseDetector.setSensitivity(60);
			freqDetector = new BeatDetect(source.bufferSize(), source.sampleRate());
			freqDetector.setSensitivity(5);
			addListeners(source);
			createFFT();
		}
		
		protected void createFFT() {
			fft = new FFT(source.bufferSize(), source.sampleRate());
			fft.logAverages(15, 5); // This is a 'tuned' set of buckets that i like
			//fft.window(FourierTransform.COSINE);
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
		
		protected void addListeners(AudioSource source) {
			source.addListener(new AudioListener() {
				public void samples(float[] samps) {
					pulseDetector.detect(source.mix);
					freqDetector.detect(source.mix);
					fft.forward(source.mix);
				}

				public void samples(float[] sampsL, float[] sampsR) {
					//scale(sampsR, .5f);
					freqDetector.detect(sampsL); //dont scale the freq just yet
					floatScalar.scale(sampsL, scaleFactor); 
					pulseDetector.detect(sampsL); //Mono amplitutde detection
					fft.forward(source.mix);
				}
			});
		}
		

	}
}
