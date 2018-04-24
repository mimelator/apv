package com.arranger.apv.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Command;
import com.arranger.apv.Macro;
import com.arranger.apv.Main;
import com.arranger.apv.Main.SYSTEM_NAMES;

import processing.event.Event;

public class MacroHelper extends APVPlugin {

	protected Map<Command, Macro> macros;
	
	public MacroHelper(Main parent) {
		super(parent);
	}
	
	public void reloadConfiguration() {
		unregister();
		configure();
		register();
	}
	
	@SuppressWarnings("unchecked")
	public void configure() {
		macros = new HashMap<Command, Macro>();
		
		int index = 1; //assign an index and add to map
		Configurator configurator = parent.getConfigurator();
		List<Macro> mcs = (List<Macro>)configurator.loadAVPPlugins(SYSTEM_NAMES.MACROS, false);
		for (Iterator<Macro> it = mcs.iterator(); it.hasNext();) {
			Command cmd = Command.getCommand(String.valueOf(index++).charAt(0), Event.CTRL);
			macros.put(cmd, it.next());
		}
	}

	public Map<Command, Macro> getMacros() {
		return macros;
	}
	
	public void register() {
		macros.forEach((k, v) -> v.register(k));
	}
	
	public void unregister() {
		macros.forEach((k, v) -> v.unregister());
	}
}
