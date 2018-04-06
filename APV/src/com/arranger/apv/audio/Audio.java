package com.arranger.apv.audio;


import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

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
	
	protected BeatInfo beatInfo;
	public float scaleFactor = DEFAULT_PULSE_DECTECT_SCALAR;
	
	public Audio(Main parent, String file, int bufferSize) {
		super(parent);
		Minim minim = new Minim(parent);
		AudioSource source = (Main.AUDIO_IN) ? minim.getLineIn() : minim.loadFile(file, bufferSize);
		
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
	}
	
	public BeatInfo getBeatInfo() {
		return beatInfo;
	}
	
	public class BeatInfo {

		protected AudioSource source;
		protected BeatDetect pulseDetector;
		protected FFT fft;
		
		public BeatInfo(AudioSource source) {
			this.source = source;
			pulseDetector = new BeatDetect(); 
			pulseDetector.setSensitivity(60);
			addListener(source);
			createFFT();
		}
		
		protected void createFFT() {
			fft = new FFT(source.bufferSize(), source.sampleRate());
			fft.logAverages(15, 5); // This is a 'tuned' set of buckets that i like
		}
		
		public AudioSource getSource() {
			return source;
		}
		
		public FFT getFF() {
			return fft;
		}
		
		public BeatDetect getPulseDetector() {
			return pulseDetector;
		};

		protected void addListener(AudioSource source) {
			source.addListener(new AudioListener() {
				public void samples(float[] samps) {
					pulseDetector.detect(source.mix);
					fft.forward(source.mix);
				}

				public void samples(float[] sampsL, float[] sampsR) {
					scale(sampsL); 
					pulseDetector.detect(sampsL); //Mono amplitutde detection
					fft.forward(source.mix);
				}
			});
		}
		
		/**
		 * Scales in place  (not very functional)
		 */
		protected void scale(float [] samps) {
			for (int i=0; i<samps.length; i++) {
				samps[i] *= scaleFactor;
			}
		}
	}
}
