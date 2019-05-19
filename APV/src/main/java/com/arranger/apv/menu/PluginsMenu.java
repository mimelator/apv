package com.arranger.apv.menu;

import java.util.List;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;


public class PluginsMenu extends BaseMenu {
	
	private List<? extends APVPlugin> plugins;

	public PluginsMenu(Main parent) {
		super(parent);
		showDetails = false;
	}
	
	@Override
	public List<? extends APVPlugin> getPlugins() {
		if (plugins == null) {
			plugins = parent.getMenu().getAPVPluginList();
		}
		return plugins;
	}

	@Override
	public boolean hasChildMenus() {
		return true;
	}

	@Override
	public BaseMenu getChildMenu(int index) {
		@SuppressWarnings("unchecked")
		APV<? extends APVPlugin> system = (APV<? extends APVPlugin>)getPlugins().get(index);
		return new PluginSystemMenu(parent, system);
	}
	
	public class PluginSystemMenu extends BaseMenu {
		
		private APV<? extends APVPlugin> system;
		
		public PluginSystemMenu(Main parent, APV<? extends APVPlugin> system) {
			super(parent);
			this.system = system;
			this.drawPlugin = true;
		}

		@Override
		public List<? extends APVPlugin> getPlugins() {
			return system.getList();
		}
	}
}
