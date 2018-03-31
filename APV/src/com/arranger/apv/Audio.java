package com.arranger.apv;


import ddf.minim.AudioListener;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;

public class Audio {

	protected BeatInfo beatInfo;
	
	public Audio(Main parent, String file, int bufferSize) {
		Minim minim = new Minim(parent);
		AudioPlayer song = minim.loadFile(file, bufferSize);
		song.play();
		beatInfo = new BeatInfo(song);
	}
	
	public BeatInfo getBeatInfo() {
		return beatInfo;
	}
	
	public class BeatInfo {

		protected BeatDetect beat;
		
		public BeatInfo(AudioPlayer song) {
			beat = new BeatDetect(song.bufferSize(), song.sampleRate());
			song.addListener(new AudioListener() {
				public void samples(float[] samps) {
					beat.detect(song.mix);
				}

				public void samples(float[] sampsL, float[] sampsR) {
					beat.detect(song.mix);
				}
			});
		};
	}
}
