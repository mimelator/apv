package com.arranger.apv.audio;


import java.nio.file.Path;
import java.util.stream.IntStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem;
import com.arranger.apv.util.FileHelper;

import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.AudioSource;
import ddf.minim.Minim;

/**
 * http://code.compartmental.net/tools/minim/
 */
public class Audio extends APVPlugin {
	
	private static final String APV_LINE_IN = Main.FLAGS.LINE_IN.apvName();
	
	protected BeatInfo beatInfo;
	protected AudioSource source;
	protected Minim minim;
	protected Info currentMixerInfo;
	protected int db = 0;
	protected boolean lineIn = false;
	
	
	public Audio(Main parent, int bufferSize) {
		super(parent);
		minim = new Minim(parent);
		
		lineIn = parent.getConfigBoolean(APV_LINE_IN);
		if (!lineIn) {
			System.out.println("Not using Line In, finding first mp3 to play");
			Path mp3 = new FileHelper(parent).getFirstMp3FromMusicDir();
			source = minim.loadFile(mp3.toAbsolutePath().toString());
			AudioPlayer audioPlayer = (AudioPlayer)source;
			audioPlayer.loop();
		} else {
			configureLineIn(parent, bufferSize);
		}
		
		parent.getSetupEvent().register(() -> {
			CommandSystem cs = parent.getCommandSystem();
			cs.registerHandler(Command.AUDIO_INC, (command, source, modifiers) -> onCommand(command));
			cs.registerHandler(Command.AUDIO_DEC, (command, source, modifiers) -> onCommand(command));
		});
	}

	@SuppressWarnings("deprecation")
	protected void configureLineIn(Main parent, int bufferSize) {
		System.out.println("Attemingt to configure the audio input line in");
		Info[] mixerInfo = AudioSystem.getMixerInfo();
		for (Info info : mixerInfo) {
			currentMixerInfo = info;
			System.out.println("trying to get the mixer from info: " + info.getName() + ":" + info.getDescription());
			Mixer mixer = AudioSystem.getMixer(info);
			minim.setInputMixer(mixer);
		    source = minim.getLineIn(Minim.MONO, bufferSize);
			try {
				boolean result = connectLineIn(parent, source);
				if (result == true) {
					return;
				}
				
//				if (connectLineIn(parent, source)) {
//					return;
//				}
			} catch (Throwable t) {
				System.out.print(t.getMessage());
			}
		}
	}

	protected boolean connectLineIn(Main parent, AudioSource source) {
		beatInfo = new BeatInfo(parent, source);
		if (source instanceof AudioInput) {
			AudioInput audioInput = (AudioInput)source;
			if (audioInput.isMonitoring()) {
				audioInput.disableMonitoring();
			}
			return true;
		}
		return false;
	}
	
	public boolean isLineIn() {
		return lineIn;
	}

	public void onCommand(Command command) {
		int offset = 1;
		String arg = command.getPrimaryArg();
		if (arg != null) {
			offset = Integer.parseInt(arg);
		}
		
		if (command.equals(Command.AUDIO_DEC)) {
			offset = -offset;
		}
		
		db += offset;
		
//		if (db < 0) {
//			System.out.println(db);
//		}
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
	
	public Info getCurrentMixerInfo() {
		return currentMixerInfo;	
	}
	
	public static void scale(float [] samples, float dBvalue) {
		float scalar = (float)Math.pow(10.0, (0.05 * dBvalue));
		IntStream.range(0, samples.length).parallel().forEach(i -> {
			samples[i] *= scalar;
		});
	}
}
