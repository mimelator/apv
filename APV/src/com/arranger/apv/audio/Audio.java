package com.arranger.apv.audio;


import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import ddf.minim.AudioInput;
import ddf.minim.AudioListener;
import ddf.minim.AudioPlayer;
import ddf.minim.AudioSource;
import ddf.minim.Minim;

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
		AudioSource source = (Main.AUDIO_IN) ? minim.getLineIn() : minim.loadFile(file, bufferSize) ;
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

		protected APVBeatDetector freqDetector;
		protected APVBeatDetector pulseDetector;
		
		public BeatInfo(AudioSource source) {
			freqDetector = new APVBeatDetector(parent, source.bufferSize(), source.sampleRate());
			pulseDetector = new APVBeatDetector(parent); 
			pulseDetector.setSensitivity(60);
			addListeners(source);
		}

		protected void addListeners(AudioSource source) {
			source.addListener(new AudioListener() {
				public void samples(float[] samps) {
					freqDetector.detect(source.mix);
					pulseDetector.detect(source.mix);
				}

				public void samples(float[] sampsL, float[] sampsR) {
					freqDetector.detect(source.mix); //Stereo detection
					scale(sampsL); 
					pulseDetector.detect(sampsL); //Mono amplitutde detection
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

		public APVBeatDetector getPulseDetector() {
			return pulseDetector;
		};
		
		public APVBeatDetector getFreqDetector() {
			return freqDetector;
		};
	}
}
