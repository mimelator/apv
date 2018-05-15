package com.arranger.apv.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.arranger.apv.Main;


public class EmojisModel extends APVModel {
	
	public static final String MESSAGE_KEY = "apvMessages";

	private List<String> msgList;
	
	public EmojisModel(Main parent) {
		super(parent);
		
		msgList = parent.getConfigurator().getRootConfig().getStringList(MESSAGE_KEY);
	}

	@Override
	public void reset() {
		msgList = parent.getConfigurator().getRootConfig().getStringList(MESSAGE_KEY);
	}

	@Override
	public void randomize() {
		Collections.shuffle(msgList);
	}

	public List<String> getMsgList() {
		return new ArrayList<String>(msgList);
	}

	public void setMsgList(List<String> msgList) {
		this.msgList = new ArrayList<String>(msgList);
	}
}
