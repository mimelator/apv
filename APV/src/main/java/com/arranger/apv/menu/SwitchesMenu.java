package com.arranger.apv.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.helpers.Switch;

public class SwitchesMenu extends BaseMenu {

	public SwitchesMenu(Main parent) {
		super(parent, false);
	}

	@Override
	public List<? extends APVPlugin> getPlugins() {
		Collection<Switch> values = parent.getSwitches().values();
		return new ArrayList<Switch>(values);
	}
}
