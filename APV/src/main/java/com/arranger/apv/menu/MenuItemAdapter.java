package com.arranger.apv.menu;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.menu.MenuPainter.MenuItem;

public class MenuItemAdapter implements MenuItem {

	private APVPlugin plugin;
	
	public MenuItemAdapter(APVPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean isEnabled() {
		return plugin.isEnabled();
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public String getText() {
		return plugin.getDisplayName();
	}

}
