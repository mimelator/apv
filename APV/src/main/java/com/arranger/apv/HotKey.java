package com.arranger.apv;

import com.arranger.apv.CommandSystem.CommandHandler;
import com.arranger.apv.util.Configurator;

import processing.event.KeyEvent;

public class HotKey extends APVPlugin implements CommandHandler {
	
	public enum SYSTEM {FOREGROUNDS, BACKGROUNDS, BACKDROPS, FILTERS, TRANSITIONS};

	private SYSTEM system;
	private String pluginName;
	private Command cmd;
	
	public HotKey(Main parent, SYSTEM system, String pluginName) {
		super(parent);
		this.system = system;
		this.pluginName = pluginName;
	}
	
	public HotKey(Configurator.Context ctx) {
		this(ctx.getParent(), 
				SYSTEM.valueOf(ctx.getString(0, SYSTEM.BACKGROUNDS.name())),
				ctx.getString(1, null));
	}
	
	@Override
	public String getConfig() {
		//{HotKey : [BACKGROUNDS, FreqDetector]}
		return String.format("{%1s : [%2s, %3s]}", getName(), system.name(), pluginName);
	}
	
	@Override
	public void onKeyPressed(KeyEvent event) {
		System.out.printf("%1s %2s %3s\n", cmd.getKey(), system.name(), pluginName);
		
		// TODO Auto-generated method stub
		//Do something good with this
		
	}

	public void registerHotKey(Command cmd) {
		this.cmd = cmd;
		
		//update help text
		cmd.setHelpText(String.format("Triggers the %1s plugin", pluginName));
		cmd.setDisplayName(String.format("HotKey[%1s %2s]", system.name(), pluginName));
		
		parent.getCommandSystem().registerHandler(cmd, this);
	}
}
