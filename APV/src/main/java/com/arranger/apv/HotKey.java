package com.arranger.apv;

import java.util.logging.Logger;

import com.arranger.apv.CommandSystem.CommandHandler;
import com.arranger.apv.util.Configurator;

import processing.event.KeyEvent;

public class HotKey extends APVPlugin implements CommandHandler {
	
	private static final Logger logger = Logger.getLogger(HotKey.class.getName());
	
	private Main.SYSTEM_NAMES system;
	private String pluginName;
	private Command cmd;
	
	public HotKey(Main parent, Main.SYSTEM_NAMES system, String pluginName) {
		super(parent);
		this.system = system;
		this.pluginName = pluginName;
	}
	
	public HotKey(Configurator.Context ctx) {
		this(ctx.getParent(), 
				Main.SYSTEM_NAMES.valueOf(ctx.getString(0, Main.SYSTEM_NAMES.BACKGROUNDS.name())),
				ctx.getString(1, null));
	}
	
	@Override
	public String getConfig() {
		//{HotKey : [BACKGROUNDS, FreqDetector]}
		return String.format("{%1s : [%2s, %3s]}", getName(), system.name(), pluginName);
	}
	
	@Override
	public void onKeyPressed(KeyEvent event) {
		parent.activateNextPlugin(pluginName, system);
	}
	
	public void unregisterHotKey() {
		if (!parent.getCommandSystem().unregisterHandler(cmd, this)) {
			logger.warning("Unable to unregister command: " + cmd.getDisplayName());
		}
	}
	
	public void registerHotKey(Command cmd) {
		this.cmd = cmd;
		//update help text
		cmd.setHelpText(String.format("Triggers the %1s plugin", pluginName));
		cmd.setDisplayName(String.format("HotKey[%1s %2s]", system.name(), pluginName));
		
		parent.getCommandSystem().registerHandler(cmd, this);
	}
}
