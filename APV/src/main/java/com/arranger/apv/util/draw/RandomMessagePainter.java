package com.arranger.apv.util.draw;

import java.util.List;
import java.util.stream.Collectors;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.FontHelper;

import processing.core.PFont;

public class RandomMessagePainter extends APVPlugin {
	
	private static final String MESSAGE_KEY = "apvMessages";
	
	private List<String> msgList = null;

	public RandomMessagePainter(Main parent) {
		super(parent);
		msgList = parent.getConfigurator().getRootConfig().getStringList(MESSAGE_KEY);
		
		//prepare fonts
		parent.getSetupEvent().register(() -> {
			FontHelper fh = parent.getFontHelper();
			PFont font = fh.createFontForText(msgList.stream().collect(Collectors.joining()));
			fh.setDefaultFont(font);
		});
		
		parent.getRandomMessageEvent().register(() -> {
			parent.sendMessage(new String[]{getMessage()});
		});
	}

	private String getMessage() {
		return msgList.get((int)parent.random(msgList.size()));
	}
}