package com.arranger.apv.audio;


import com.arranger.apv.APVPlugin;
import com.arranger.apv.CommandSystem;
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
		
		CommandSystem cs = parent.getCommandSystem();
		cs.registerCommand('+', "Audio", "Increases the audio sensitivity", event -> scaleFactor++);
		cs.registerCommand('-', "Audio", "Decreases the audio sensitivity", event -> scaleFactor--);
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
					scale(sampsR, .5f);
					freqDetector.detect(sampsL); //dont scale the freq just yet
					scale(sampsL, scaleFactor); 
					pulseDetector.detect(sampsL); //Mono amplitutde detection
					fft.forward(source.mix);
				}
			});
		}
		
		/**
		 * Scales in place  (not very functional)
		 * When the sf goes below Zero, the following happens: 1 x 10^sf
		 * 
		 * So, -3 becomes 10^-3 or .0001
		 * 
		 * TODO Needs Test
		 */
		protected void scale(float [] samps, float sf) {
			if (sf < 0) {
				sf = (float)Math.pow(10, sf);
			}
			
			for (int i=0; i<samps.length; i++) {
				samps[i] *= sf;
			}
		}
	}
}
