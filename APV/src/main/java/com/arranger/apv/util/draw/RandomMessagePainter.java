package com.arranger.apv.util.draw;

import java.util.List;
import java.util.stream.Collectors;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.model.EmojisModel;
import com.arranger.apv.util.FontHelper;

import processing.core.PFont;

public class RandomMessagePainter extends APVPlugin {
	
	private EmojisModel model;
	
	public RandomMessagePainter(Main parent) {
		super(parent);
		model = parent.getEmojisModel();
		initializeFonts(parent);
		
		parent.getRandomMessageEvent().register(() -> {
			parent.sendMessage(new String[]{getRandomMessage()});
		});
	}
	
	public void reset() {
		initializeFonts(parent);
	}
	
	@Override
	public String getConfig() {
		return parent.getConfigurator().generateConfig(EmojisModel.MESSAGE_KEY, model.getMsgList(), false, true);
	}
	
	public String getRandomMessage() {
		List<String> list = model.getMsgList();
		return list.get((int)parent.random(list.size()));
	}
	
	private void initializeFonts(Main parent) {
		parent.getSetupEvent().register(() -> {
			FontHelper fh = parent.getFontHelper();
			PFont font = fh.createFontForText(model.getMsgList().stream().collect(Collectors.joining()));
			if (font != null) {
				fh.setCurrentFont(font);
			}
		});
	}
}
