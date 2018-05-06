package com.arranger.apv.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	private static final Logger logger = Logger.getLogger(APVSetList.class.getName());
	
	private static final String KEY = "apv.setListFolder";
	private static final String CONFIG = "apv.setListFolder = %s";
	private List<Path> setList = new ArrayList<Path>();
	private AudioPlayer currentPlayer;
	private String configDirectory = "songs";
	
	public APVSetList(Main parent){
		super(parent);
	}
	
	@Override
	public String getConfig() {
		return String.format(CONFIG, configDirectory) + System.lineSeparator();
	}
	
	public void setConfigDirectory(String configDirectory) {
		this.configDirectory = configDirectory;
	}
	
	public void play() {
		String configString = parent.getConfigString(KEY);
		Path currentDir = Paths.get(".");
		Path songFolder = currentDir.resolve(configString);
		play(songFolder.toFile());
	}
	
	public void stop() {
		if (currentPlayer != null) {
			currentPlayer.close();
		}
		setList.clear();
	}
	
	public void play(List<File> files, int indexToStart) {
		setList.clear();
		files.forEach(f -> setList.add(f.toPath()));
		playSetList(indexToStart);
	}
	
	public void play(File directory) {
		setList.clear();
		
		if (directory.isDirectory()) {
			try {
				Files.newDirectoryStream(Paths.get(directory.getAbsolutePath()), "*.mp3").forEach(s -> setList.add(s));
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				return;
			}
		} else {
			setList.add(directory.toPath());
		}
		
		playSetList(0);
	}
	
	public List<Path> getSetList() {
		return setList;
	}
	
	protected void playSetList(int index) {
		Minim minim = parent.getAudio().getMinim();
		new Thread(() -> {
			try {
				ListIterator<Path> it = setList.listIterator(index);
				while (it.hasNext()) {
					playSong(minim.loadFile(it.next().toString()));
				}
			} catch (Exception e) {
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
