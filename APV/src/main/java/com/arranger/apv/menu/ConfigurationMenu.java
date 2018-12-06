package com.arranger.apv.menu;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public class ConfigurationMenu extends CommandBasedMenu {

	public ConfigurationMenu(Main parent) {
		super(parent);
		showDetails = false;
	}

	@Override
	public List<? extends APVPlugin> getPlugins() {
		List<MenuAdapterCallback> results = new ArrayList<MenuAdapterCallback>();
		
		results.add(new MenuAdapterCallback(parent, Command.SAVE_CONFIGURATION.getDisplayName(), ()-> onSave()));
		results.add(new MenuAdapterCallback(parent, Command.LOAD_CONFIGURATION.getDisplayName(), ()-> onLoad()));
		results.add(new MenuAdapterCallback(parent, Command.RELOAD_CONFIGURATION.getDisplayName(), ()-> fireCommand(Command.RELOAD_CONFIGURATION)));
		
		return results;
	}

	protected void onSave() {
		parent.selectFile("Select configuration file", file -> {
			parent.getConfigurator().saveCurrentConfig(file);
		});
	}
	
	protected void onLoad() {
		parent.selectFile("Select configuration file", file -> {
			parent.getConfigurator().reload(file.getAbsolutePath());
		});
	}
}
