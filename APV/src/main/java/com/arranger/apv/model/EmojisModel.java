package com.arranger.apv.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.db.entity.MessageEntity;


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
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadFromEntities(List entities) {
		List<MessageEntity> meList = (List<MessageEntity>)entities;
		msgList.clear();
		
		meList.forEach(me -> {
			msgList.add(me.getMessage());
		});
	}

	public List<String> getMsgList() {
		return new ArrayList<String>(msgList);
	}

	public void setMsgList(List<String> msgList) {
		this.msgList = new ArrayList<String>(msgList);
	}
}
