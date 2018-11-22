package com.arranger.apv.menu;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.menu.MenuPainter.MenuItem;

public class MenuItemAdapter implements MenuItem {

	private APVPlugin plugin;
	private boolean selected;
	
	public MenuItemAdapter(APVPlugin plugin, boolean selected) {
		this.plugin = plugin;
		this.selected = selected;
	}

	@Override
	public boolean isEnabled() {
		return plugin.isEnabled();
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public String getText() {
		return plugin.getDisplayName();
	}

	@Override
	public APVPlugin getPlugin() {
		return plugin;
	}
}
