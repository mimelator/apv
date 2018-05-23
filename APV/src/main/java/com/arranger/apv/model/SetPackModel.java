package com.arranger.apv.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.util.FileHelper;

public class SetPackModel extends APVModel {

	private List<String> setPackList = new ArrayList<String>();
	private FileHelper fh;
	private int index;
	
	public SetPackModel(Main parent) {
		super(parent);
		fh = new FileHelper(parent);
	}

	@Override
	public void reset() {
		setPackList.clear();
		index = 0;
	}

	@Override
	public void randomize() {
		Collections.shuffle(setPackList);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public void loadFromEntities(List entities) {
		
	}

	public List<String> getSetPackList() {
		return new ArrayList<String>(setPackList);
	}

	public void setSetPackList(List<String> setPackList) {
		this.setPackList = new ArrayList<String>(setPackList);
	}

	public void launchNextSetPack() {
		launchSetPack(index++);
	}
	
	public void launchPrevSetPack() {
		launchSetPack(index--);
	}
	
	public void launchSetPack(int index) {
		if (setPackList.isEmpty()) {
			return;
		}
		
		if (index >= setPackList.size()) {
			index = 0;
		} else if (index < 0) {
			index = setPackList.size() - 1;
		}
		
		int currentIndex = index;
		
		String setPackName = setPackList.get(index);
		Path setPackPath = fh.getSetPacksFolder().toPath().resolve(setPackName);
		Path confgFile = setPackPath.resolve("application.conf");
		String configFilePath = confgFile.toAbsolutePath().toString();
		
		try {
			String result = new String(Files.readAllBytes(confgFile));
			System.out.println("Launching setpack: " + setPackName);
			System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//This will cause #reset to be triggered and we'll lose the index
		parent.reloadConfiguration(configFilePath);
		
		this.index = currentIndex + 1;
	}
}
