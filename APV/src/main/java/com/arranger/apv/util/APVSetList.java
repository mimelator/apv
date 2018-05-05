package com.arranger.apv.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import ddf.minim.AudioMetaData;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;

/**
 * Look up all of the files in the folder: 'apv.setListFolder'
 * 
 * When asked to, it will play each song.
 * At the beginning of each song it will
 * ** Optionally reload the configuration
 * ** Play one song after the next launching it's own thread
 * *** Trigger a new marquee with the title of each song
 * 
 */
public class APVSetList extends APVPlugin {

	private static final String KEY = "apv.setListFolder";
	private List<Path> setList = new ArrayList<Path>();
	private AudioPlayer currentPlayer;
	
	public APVSetList(Main parent){
		super(parent);
	}
	
	public void play() throws IOException  {
		String configString = parent.getConfigString(KEY);
		play(new File(configString));
	}
	
	public void stop() {
		if (currentPlayer != null) {
			currentPlayer.close();
		}
		setList.clear();
	}
	
	public void play(File directory) throws IOException {
		if (directory.isDirectory()) {
			Files.newDirectoryStream(Paths.get(directory.getAbsolutePath()), "*.mp3").forEach(s -> setList.add(s));
		} else {
			setList.add(directory.toPath());
		}
		
		final Minim minim = parent.getAudio().getMinim();
		new Thread(() -> {
			try {
				setList.forEach(song -> {
					playSong(minim.loadFile(song.toString()));
				});
			} catch (ConcurrentModificationException e) {
				//When the setList has been reset
			}
		}).start();
		
		Thread.yield();
	}

	protected void playSong(AudioPlayer player) {
		currentPlayer = player;
		AudioMetaData metaData = player.getMetaData();
		String title = metaData.title();
		if (title == null) {
			title = metaData.fileName();
		}
		parent.sendMarqueeMessage(title);
		player.play();
		while (player.isPlaying()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
