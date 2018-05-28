package com.arranger.apv.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.db.entity.SongEntity;
import com.arranger.apv.util.APVSetListPlayer;
import com.arranger.apv.util.FileHelper;

/**
 * SongsModel contains the currently chosen songs
 * It provides data and apis to the Songs Panel
 * It provides access to the songs for Main/SetList
 * It doesn't provide any serialization
 *
 */
public class SongsModel extends APVModel {
	
	private List<File> songs = new ArrayList<File>();
	private int index;

	public SongsModel(Main parent) {
		super(parent);
		reset();
	}

	public void onSetListPlayerChange() {
		reset();
		APVSetListPlayer setList = parent.getSetListPlayer();
		if (setList != null) {
			setList.getSetList().forEach(songPath -> {
				songs.add(songPath.toFile());
			});
		}
	}
	
	@Override
	public void reset() {
		songs.clear();
		index = 0;
		parent.getSetupEvent().register(() -> {
			onSetListPlayerChange();
		});
	}
	
	@Override
	public void randomize() {
		Collections.shuffle(songs);
		index = 0;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadFromEntities(List entities) {
		songs.clear();
		index = 0;
		FileHelper fh = new FileHelper(parent);
		List<SongEntity> seList = (List<SongEntity>)entities;
		seList.forEach(se -> {
			String fullPath = fh.getFullPath(se.getFilename());
			songs.add(new File(fullPath));
		});
		
		playSong(0);
	}
	
	public void playSong(int index) {
		if (index >= songs.size()) {
			index = 0;
		} else if (index < 0) {
			index = songs.size() - 1;
		}
		
		APVSetListPlayer setList = parent.getSetListPlayer();
		if (setList != null) {
			setList.play(songs, index);
		}
	}
	
	public void stop() {
		APVSetListPlayer setList = parent.getSetListPlayer();
		if (setList != null) {
			setList.stop();
		}
	}
	
	public void ffwd() {
		index++;
		playSong(index);
	}
	
	public void prev() {
		index--;
		playSong(index);
	}
	
	public int getIndex() {
		return index;
	}
	
	public List<File> getSongs() {
		return new ArrayList<File>(songs);
	}
	
	public void setSongs(List<File> songs) {
		this.songs = new ArrayList<File>(songs);
		index = 0;
	}
}
