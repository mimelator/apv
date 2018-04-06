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
 * 
 * beat.setSensitivity(300); {@link FreqDetector}
 *
 */
public class Audio extends APVPlugin {
	
	protected BeatInfo beatInfo;
	
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

		protected APVBeatDetector beat;
		
		public BeatInfo(AudioSource source) {
			beat = new APVBeatDetector(parent, source.bufferSize(), source.sampleRate());
			source.addListener(new AudioListener() {
				public void samples(float[] samps) {
					beat.detect(source.mix);
				}

				public void samples(float[] sampsL, float[] sampsR) {
					beat.detect(source.mix);
				}
			});
		}

		public APVBeatDetector getBeat() {
			return beat;
		};
	}
}
