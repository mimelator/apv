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
				String primaryArg = null;
				String [] args = null;
				
				if (cmdString.contains(":")) {
					String[] split = cmdString.split(":");
					cmdString = split[0];
					primaryArg = split[1];
					if (split.length > 2) {
						int newLength = split.length - 2;
						args = new String[newLength];
						System.arraycopy(split, 2, args, 0, newLength);
					}
				}
				
				try {
					Command cmd = Command.valueOf(cmdString);
					if (primaryArg != null) {
						cmd.setPrimaryArg(primaryArg);
					}
					if (args != null) {
						cmd.setArgs(args);
					}
					
					parent.getCommandSystem().invokeCommand(cmd, getDisplayName(), 0);
					cmd.reset();
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
