package com.arranger.apv.cmd;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public abstract class CommandInterceptor extends APVPlugin {
	
	protected boolean active = false;

	public CommandInterceptor(Main parent) {
		super(parent);
	}
	
	public abstract boolean intercept(char key);
	
	public abstract String getHelpText();
}