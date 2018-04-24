package com.arranger.apv.helpers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.arranger.apv.Main;
import com.arranger.apv.Main.SYSTEM_NAMES;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.util.Configurator;

public class HotKeyHelper extends APVCollectionHelper {
	
	private static final char [] HOT_KEYS = {'!', '@', '#', '$', '%', '^', '&', '*'}; //Shift + (1 through 8)
	
	protected Map<Command, HotKey> hotKeys;

	public HotKeyHelper(Main parent) {
		super(parent);
	}
	
	public Map<Command, HotKey> getHotKeys() {
		return hotKeys;
	}

	@Override
	public void register() {
		hotKeys.forEach((k, v) -> v.register(k));
	}
	
	@Override
	public void unregister() {
		hotKeys.forEach((k, v) -> v.unregister());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void configure() {
		hotKeys = new HashMap<Command, HotKey>();
		
		int index = 0; //assign an index and add to map
		Configurator configurator = parent.getConfigurator();
		List<HotKey> hks = (List<HotKey>)configurator.loadAVPPlugins(SYSTEM_NAMES.HOTKEYS, false);
		for (Iterator<HotKey> it = hks.iterator(); it.hasNext();) {
			Command cmd = Command.getCommand(HOT_KEYS[index++]);
			hotKeys.put(cmd, it.next());
		}
	}
}
