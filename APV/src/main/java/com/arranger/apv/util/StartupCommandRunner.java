package com.arranger.apv.util;

import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public class StartupCommandRunner extends APVPlugin {
	
	public static final String CMD_KEY = "defaultCommands";
	
	private List<String> cmdList;

	public StartupCommandRunner(Main parent) {
		super(parent);
	}
	
	public void runStartupCommands() {
		cmdList = parent.getConfigurator().getRootConfig().getStringList(CMD_KEY);
		runCommands(cmdList);
	}

	protected void runCommands(List<String> cmdList) {
		cmdList.forEach(cmdString -> {
			if (!cmdString.isEmpty() && !cmdString.startsWith("#")) {
				Command cmd = Command.valueOf(cmdString);
				parent.getCommandSystem().invokeCommand(cmd, getDisplayName(), 0);
			}
		});
	}

	@Override
	public String getConfig() {
		return parent.getConfigurator().generateConfig(CMD_KEY, cmdList, false, true);
	}
}
