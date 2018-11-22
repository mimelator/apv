package com.arranger.apv.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class PluginsMenu extends BaseMenu {

	public PluginsMenu(Main parent) {
		super(parent, false);
	}

	@Override
	public List<? extends APVPlugin> getPlugins() {
		Collection<APV<? extends APVPlugin>> systems = parent.getSystems();
		
		@SuppressWarnings("rawtypes")
		List<APV> results = new ArrayList<APV>();
		for (APV<? extends APVPlugin> system : systems) {
			if (system.getSystemName().isFullSystem) {
				results.add(system);
			}
		}
		
		return results;
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
			super(parent, false);
			this.system = system;
		}

		@Override
		public List<? extends APVPlugin> getPlugins() {
			return system.getList();
		}
	}
	
}
