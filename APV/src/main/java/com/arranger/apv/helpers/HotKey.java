package com.arranger.apv.helpers;

import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem.CommandHandler;
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
		return String.format("{%s : [%s, %s]}", getName(), system.name(), pluginName);
	}
	
	@Override
	public void onKeyPressed(KeyEvent event) {
		try {
			parent.activateNextPlugin(pluginName, system, "hotKey");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void unregister() {
		if (!parent.getCommandSystem().unregisterHandler(cmd, this)) {
			logger.warning("Unable to unregister command: " + cmd.getDisplayName());
		}
	}
	
	public void register(Command cmd) {
		this.cmd = cmd;
		//update help text
		cmd.setHelpText(String.format("Triggers the %s plugin", pluginName));
		cmd.setDisplayName(String.format("HotKey[%s %s]", system.name(), pluginName));
		
		parent.getCommandSystem().registerHandler(cmd, this);
	}
}
