package com.arranger.apv.audio;


import java.util.stream.IntStream;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem;

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
	protected Minim minim;
	protected int db = 0;
	
	public Audio(Main parent, int bufferSize) {
		super(parent);
		minim = new Minim(parent);
		//source = minim.getLineIn(Minim.MONO, bufferSize);
		source = minim.loadFile("C:\\Users\\Administrator\\Music\\stellar.mp3");
		
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
		
		parent.getSetupEvent().register(() -> {
				CommandSystem cs = parent.getCommandSystem();
				cs.registerHandler(Command.AUDIO_INC, event -> db++);
				cs.registerHandler(Command.AUDIO_DEC, event -> db--);
		});
	}
	
	public float getDB() {
		return db;
	}
	
	public Minim getMinim() {
		return minim;
	}

	public BeatInfo getBeatInfo() {
		return beatInfo;
	}
	
	public class BeatInfo {

		protected AudioSource source;
		protected BeatDetect pulseDetector;
		protected BeatDetect freqDetector;
		protected FFT fft;
		protected AudioListener listener;
		
		public BeatInfo(AudioSource source) {
			this.source = source;
			pulseDetector = new BeatDetect(); 
			pulseDetector.setSensitivity(60);
			freqDetector = new BeatDetect(source.bufferSize(), source.sampleRate());
			freqDetector.setSensitivity(5);
			createListener();
			source.addListener(listener);
			createFFT();
		}
		
		public void updateSource(AudioSource newSource) {
			if (source != null) {
				source.removeListener(listener);
				if (source instanceof AudioPlayer) {
					((AudioPlayer)source).close();
				}
			}
			
			source = newSource;
			newSource.addListener(listener);
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
		
		private void createListener() {
			this.listener =  new AudioListener() {
				public void samples(float[] samps) {
					scale(samps, db);
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
	
	protected void scale(float [] samples, float dBvalue) {
		float scalar = (float)Math.pow(10.0, (0.05 * dBvalue));
		IntStream.range(0, samples.length).parallel().forEach(i -> {
			samples[i] *= scalar;
		});
	}
}
