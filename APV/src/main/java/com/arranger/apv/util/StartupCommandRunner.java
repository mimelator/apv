package com.arranger.apv.util;

import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public class StartupCommandRunner extends APVPlugin {
	
	public static final String CMD_KEY = "defaultCommands";

	public StartupCommandRunner(Main parent) {
		super(parent);
	}
	
	public void runStartupCommands() {
		List<String> cmdList = parent.getConfigurator().getRootConfig().getStringList(CMD_KEY);
		cmdList.forEach(cmdString -> {
			Command cmd = Command.valueOf(cmdString);
			parent.getCommandSystem().invokeCommand(cmd, getDisplayName(), 0);
		});
	}
}
