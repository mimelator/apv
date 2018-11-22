package com.arranger.apv.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.helpers.Switch;

public class SwitchesMenu extends BaseMenu {
	
	protected List<Switch> switches;

	public SwitchesMenu(Main parent) {
		super(parent);
	}

	@Override
	public List<? extends APVPlugin> getPlugins() {
		if (switches == null) {
			switches = createSwitchList();
		}
		return switches;
	}
	
	protected List<Switch> createSwitchList() {
		Map<String, Switch> map = parent.getSwitches();
		List<Switch> results = new ArrayList<Switch>();
		results.add(map.get(Main.SWITCH_NAMES.AUDIO_LISTENER_DIAGNOSTIC.name));
		results.add(map.get(Main.SWITCH_NAMES.CONSOLE_OUTPUT.name));
		results.add(map.get(Main.SWITCH_NAMES.DEBUG_AGENT.name));
		results.add(map.get(Main.SWITCH_NAMES.DEBUG_PULSE.name));
		results.add(map.get(Main.SWITCH_NAMES.HELP.name));
		results.add(map.get(Main.SWITCH_NAMES.SCRAMBLE_MODE.name));
		results.add(map.get(Main.SWITCH_NAMES.SHOW_SETTINGS.name));
		results.add(map.get(Main.SWITCH_NAMES.VIDEO_GAME.name));
		results.add(map.get(Main.SWITCH_NAMES.WELCOME.name));
		
		for (APVPlugin system : parent.getMenu().getAPVPluginList()) {
			@SuppressWarnings("unchecked")
			APV<? extends APVPlugin> apv = (APV<? extends APVPlugin>)system;
			Switch sw = apv.getSwitch();
			if (sw != null) {
				results.add(sw);
			}
		}
		
		return results;
	}
}
