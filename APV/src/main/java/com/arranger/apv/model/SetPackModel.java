package com.arranger.apv.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import com.arranger.apv.Main;
import com.arranger.apv.control.ControlSystem.CONTROL_MODES;
import com.arranger.apv.db.entity.SetpackEntity;
import com.arranger.apv.util.FileHelper;

public class SetPackModel extends APVModel {

	private List<String> setPackList = new ArrayList<String>();
	private FileHelper fh;
	private int index;
	
	private SetpackEntity setpackEntity;
	private String setPackName;
	
	public SetPackModel(Main parent) {
		super(parent);
		fh = new FileHelper(parent);
	}
	
	public SetpackEntity getSetpackEntity() {
		return setpackEntity;
	}

	public void setSetpackEntity(SetpackEntity setpackEntity) {
		this.setpackEntity = setpackEntity;
	}

	@Override
	public void reset() {
		setPackList.clear();
	}

	@Override
	public void randomize() {
		Collections.shuffle(setPackList);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public void loadFromEntities(List entities) {
		
	}
	
	public void playSetPack(String setPackName) {
		IntStream.range(0, setPackList.size()).forEach(i -> {
			String setPack = setPackList.get(i);
			if (setPackName.equalsIgnoreCase(setPack)) {
				launchSetPack(i);
			}
		});
	}

	public List<String> getSetPackList() {
		return new ArrayList<String>(setPackList);
	}

	public void setSetPackList(List<String> setPackList) {
		this.setPackList = new ArrayList<String>(setPackList);
	}
	
	public String getSetPackName() {
		return setPackName;
	}

	public void launchNextSetPack() {
		launchSetPack(index + 1);
	}
	
	public void launchPrevSetPack() {
		launchSetPack(index - 1);
	}
	
	public void launchSetPack(int newIndex) {
		System.out.println("incomingIndex: " + newIndex);
		System.out.println("incomingIndex: " + newIndex);
		System.out.println("incomingIndex: " + newIndex);
		if (setPackList.isEmpty()) {
			return;
		}
		
		if (newIndex >= setPackList.size()) {
			newIndex = 0;
		} else if (newIndex < 0) {
			newIndex = setPackList.size() - 1;
		}
		
		setPackName = setPackList.get(newIndex);
		
		//preserve some state before reload
		CONTROL_MODES currentControlMode = parent.getCurrentControlMode();
		SetpackEntity setpackEntity = parent.getDBSupport().findSetpackEntityByName(setPackName);
		List<String> prevSetPackList = getSetPackList();
		
		//start working on reload
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
		
		//This will cause #reset to be triggered 
		parent.reloadConfiguration(configFilePath);
		
		//restore some state
		parent.setCurrentControlMode(currentControlMode);
		this.setpackEntity = setpackEntity;
		setSetPackList(prevSetPackList);
		this.index = setPackList.indexOf(setPackName);
		System.out.println("postIndex: " + this.index);
		System.out.println("postIndex: " + this.index);
		System.out.println("postIndex: " + this.index);
		
		parent.getSetPackStartEvent().fire();
	}
}
