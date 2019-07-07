package com.arranger.apv.menu;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public class ConfigurationMenu extends CommandBasedMenu {

	private String successMessage;
	
	public ConfigurationMenu(Main parent) {
		super(parent);
		showDetails = false;
	}

	@Override
	public List<? extends APVPlugin> getPlugins() {
		List<MenuAdapterCallback> results = new ArrayList<MenuAdapterCallback>();
		
		results.add(new MenuAdapterCallback(parent, Command.SAVE_CONFIGURATION.getDisplayName(), ()-> onSave()));
		results.add(new MenuAdapterCallback(parent, Command.LOAD_CONFIGURATION.getDisplayName(), ()-> onLoad()));
		results.add(new MenuAdapterCallback(parent, Command.RELOAD_CONFIGURATION.getDisplayName(), ()-> onReload()));
		
		return results;
	}
	
	@Override
	public void onActivate() {
		super.onActivate();
		successMessage = null;
	}

	@Override
	protected boolean shouldDrawResultMsg(List<String> msgs) {
		if (successMessage != null) {
			msgs.add(successMessage);
			return true;
		} else {
			return false;
		}
	}
	
	protected void onReload() {
		loadConfiguration(null);
	}

	protected void onSave() {
		parent.selectFile("Select configuration file", file -> {
			parent.getConfigurator().saveCurrentConfig(file);
			successMessage = "Saved File: " + file.getName();
		});
	}
	
	protected void onLoad() {
		parent.selectInputFile("Select configuration file", file -> {
			loadConfiguration(file.getAbsolutePath());
		});
	}

	protected void loadConfiguration(String configFile) {
		parent.reloadConfiguration(configFile, () -> {
			parent.getMenu().toggleEnabled();
			parent.restoreCommandSystem();
		});
	}
}
