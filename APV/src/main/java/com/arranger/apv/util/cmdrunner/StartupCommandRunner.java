package com.arranger.apv.util.cmdrunner;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public class StartupCommandRunner extends APVPlugin {
	
	private static final Logger logger = Logger.getLogger(StartupCommandRunner.class.getName());
	
	public static final String CMD_KEY = "defaultCommands";
	
	private List<String> cmdList;

	public StartupCommandRunner(Main parent) {
		super(parent);
	}
	
	public void runStartupCommands() {
		cmdList = parent.getConfigurator().getRootConfig().getStringList(CMD_KEY);
		runCommands(cmdList);
	}

	public void runCommands(List<String> cmdList) {
		cmdList.forEach(cmdString -> {
			if (!cmdString.isEmpty() && !cmdString.startsWith("#")) {
				String argument = null;
				if (cmdString.contains(":")) {
					String[] split = cmdString.split(":");
					cmdString = split[0];
					argument = split[1];
				}
				
				try {
					Command cmd = Command.valueOf(cmdString);
					if (argument != null) {
						cmd.setArgument(argument);
					}
					
					parent.getCommandSystem().invokeCommand(cmd, getDisplayName(), 0);
				} catch (Exception e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		});
	}

	@Override
	public String getConfig() {
		return parent.getConfigurator().generateConfig(CMD_KEY, cmdList, false, true);
	}
}
