package com.arranger.apv.audio;


import com.arranger.apv.APVPlugin;
import com.arranger.apv.Command;
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
	
	protected BeatInfo beatInfo;
	protected AudioSource source;
	public float gain;
	
	public Audio(Main parent, int bufferSize) {
		super(parent);
		Minim minim = new Minim(parent);
		source = minim.getLineIn(Minim.MONO, bufferSize);
		
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
		gain = source.getGain();
		
		parent.getSetupEvent().register(() -> {
				CommandSystem cs = parent.getCommandSystem();
				cs.registerHandler(Command.AUDIO_INC, event -> adjustGain(gain + 1));
				cs.registerHandler(Command.AUDIO_DEC, event -> adjustGain(gain -1));
		});
	}
	
	public void adjustGain(float gain) {
		this.gain = gain;
		source.setGain(gain);
	}
	
	public float getGain() {
		return gain;
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
					freqDetector.detect(sampsL); 
					pulseDetector.detect(sampsL); 
					fft.forward(sampsL);
				}
			});
		}
		

	}
}
