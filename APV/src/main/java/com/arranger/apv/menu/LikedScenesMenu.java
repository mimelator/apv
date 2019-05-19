package com.arranger.apv.menu;

import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class LikedScenesMenu extends BaseMenu {

	public LikedScenesMenu(Main parent) {
		super(parent);
		this.drawPlugin = true;
	}

	@Override
	public List<? extends APVPlugin> getPlugins() {
		return parent.getLikedScenes();
	}

}
