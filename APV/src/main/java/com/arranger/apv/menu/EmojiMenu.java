package com.arranger.apv.menu;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.model.EmojisModel;

public class EmojiMenu extends CommandBasedMenu {
	
	public EmojiMenu(Main parent) {
		super(parent);
	}

	@Override
	public List<? extends APVPlugin> getPlugins() {
		List<MenuAdapterCallback> results = new ArrayList<MenuAdapterCallback>();
		
		parent.getEmojisModel().getMsgList().forEach(msg -> {
			results.add(new MenuAdapterCallback(parent, msg, ()-> updateEmoji(msg)));
		});
		
		return results;
	}

	protected void updateEmoji(String orig) {
		String result = JOptionPane.showInputDialog(orig);
		if (result != null) {
			EmojisModel emojisModel = parent.getEmojisModel();
			List<String> msgList = emojisModel.getMsgList();
			int i = msgList.indexOf(orig);
			if (i != -1) {
				msgList.set(i, result);
				emojisModel.setMsgList(msgList);
				shouldSaveOnDeactivate = true;
			}
		}
	}
}
