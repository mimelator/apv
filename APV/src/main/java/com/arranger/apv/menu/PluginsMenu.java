package com.arranger.apv.menu;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.Main.SYSTEM_NAMES;


public class PluginsMenu extends BaseMenu {
	
	private List<? extends APVPlugin> plugins;

	public PluginsMenu(Main parent) {
		super(parent);
		showDetails = false;
	}
	
	@Override
	public List<? extends APVPlugin> getPlugins() {
		if (plugins == null) {
			plugins = createPluginList();
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
	
	protected List<? extends APVPlugin> createPluginList() {
		List<APV<? extends APVPlugin>> results = new ArrayList<APV<? extends APVPlugin>>();
		results.add(parent.getAgent());
		results.add(parent.getBackgrounds());
		results.add(parent.getBackDrops());
		results.add(parent.getSystem(SYSTEM_NAMES.CONTROLS));
		results.add(parent.getFilters());
		results.add(parent.getForegrounds());
		results.add(parent.getLocations());
		results.add(parent.getShaders());
		results.add(parent.getTransitions());
		results.add(parent.getWatermark());
		return results;
	}
	
	public class PluginSystemMenu extends BaseMenu {
		
		private APV<? extends APVPlugin> system;
		
		public PluginSystemMenu(Main parent, APV<? extends APVPlugin> system) {
			super(parent);
			this.system = system;
		}

		@Override
		public List<? extends APVPlugin> getPlugins() {
			return system.getList();
		}
	}
}
