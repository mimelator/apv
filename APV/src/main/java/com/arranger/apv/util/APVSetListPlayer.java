package com.arranger.apv.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.db.entity.SetpackEntity;

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
public class APVSetListPlayer extends APVPlugin {

	private static final String KEY = Main.FLAGS.SET_LIST_FOLDER.apvName();
	private static final String CONFIG = KEY + " = ${apv.setPack.home}" + File.separator + "%s";

	
	private List<Path> songList = new ArrayList<Path>();
	private AudioPlayer currentPlayer;
	private String relConfigDir = "songs";
	
	private String currentSongTitle;
	
	public APVSetListPlayer(Main parent){
		super(parent);
	}
	
	@Override
	public String getConfig() {
		return String.format(CONFIG, relConfigDir) + System.lineSeparator();
	}
	
	public void setRelativeConfigDirectory(String relConfigDir) {
		this.relConfigDir = relConfigDir;
	}
	
	public void playStartupSongList() {
		String configString = parent.getConfigString(KEY);
		Path currentDir = Paths.get(".");
		Path songFolder = currentDir.resolve(configString);
		play(songFolder.toFile());
	}
	
	public void stop() {
		if (currentPlayer != null) {
			currentPlayer.close();
		}
		songList.clear();
	}
	
	public void play(List<File> files, int indexToStart) {
		songList.clear();
		files.forEach(f -> songList.add(f.toPath()));
		playSongList(indexToStart);
	}
	
	public void play(File directory) {
		songList.clear();
		
		if (directory.isDirectory()) {
			songList = new FileHelper(parent).getAllMp3sFromDir(directory.toPath());
		} else {
			songList.add(directory.toPath());
		}
		
		playSongList(0);
	}
	
	public List<Path> getSetList() {
		return songList;
	}
	
	public String getCurrentSongTitle() {
		return currentSongTitle;
	}
	
	protected void playSongList(int index) {
		Path path2 = songList.get(index);
		String name = path2.getParent().getParent().toFile().getName();
		SetpackEntity setPackEntity = parent.getDBSupport().findSetpackEntityByName(name);
		parent.getSetPackModel().setSetpackEntity(setPackEntity);
		
		Minim minim = parent.getAudio().getMinim();
		new Thread(() -> {
			try {
				ListIterator<Path> it = songList.listIterator(index);
				while (it.hasNext()) {
					playSong(minim.loadFile(it.next().toString()));
				}
				
				//fire setlist ended event
				parent.getSetListCompleteEvent().fire();
				
			} catch (Exception e) {
				//When the setList has been reset
			}
		}).start();
		
		Thread.yield();
	}

	protected void playSong(AudioPlayer player) {
		currentPlayer = player;
		parent.getAudio().getBeatInfo().updateSource(player);
		AudioMetaData metaData = player.getMetaData();
		String title = metaData.title();
		if (title == null || title.isEmpty()) {
			title = new File(metaData.fileName()).getName();
		}
		currentSongTitle = title;
		parent.getSongStartEvent().fire();
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
