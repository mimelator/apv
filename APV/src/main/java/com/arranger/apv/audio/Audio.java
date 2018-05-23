package com.arranger.apv.audio;


import java.nio.file.Path;
import java.util.stream.IntStream;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem;
import com.arranger.apv.util.FileHelper;

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
	
	private static final String APV_LINE_IN = Main.FLAGS.LINE_IN.apvName();
	
	protected BeatInfo beatInfo;
	protected AudioSource source;
	protected Minim minim;
	protected int db = 0;
	protected boolean lineIn = false;
	
	public Audio(Main parent, int bufferSize) {
		super(parent);
		minim = new Minim(parent);
		
		boolean lineIn = parent.getConfigBoolean(APV_LINE_IN);
		if (lineIn) {
			System.out.println("Listening to line in");
			lineIn = true;
			source = minim.getLineIn(Minim.MONO, bufferSize);
		} else {
			System.out.println("Not using Line In, finding first mp3 to play");
			Path mp3 = new FileHelper(parent).getFirstMp3FromMusicDir();
			source = minim.loadFile(mp3.toAbsolutePath().toString());
		}
		
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
			cs.registerHandler(Command.AUDIO_INC, (command, source, modifiers) -> db++);
			cs.registerHandler(Command.AUDIO_DEC, (command, source, modifiers) -> db--);
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
			setup(source);
		}

		protected void setup(AudioSource source) {
			this.source = source;
			pulseDetector = new BeatDetect(); 
			pulseDetector.setSensitivity(60);
			freqDetector = new BeatDetect(source.bufferSize(), source.sampleRate());
			freqDetector.setSensitivity(5);
			createFFT();
			createListener();
			source.addListener(listener);
		}
		
		public void updateSource(AudioSource newSource) {
			if (!lineIn && source != null) {
				source.removeListener(listener);
			}
			
			if (source instanceof AudioPlayer) {
				((AudioPlayer)source).close();
			}
				
			setup(newSource);
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
