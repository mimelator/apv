package com.arranger.apv;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.arranger.apv.CommandSystem.CommandHandler;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.JSONQuoter;

import processing.event.KeyEvent;

public class Macro extends APVPlugin implements CommandHandler {
	
	private static final Logger logger = Logger.getLogger(Macro.class.getName());

	private List<Command> commands;
	private Command cmd;
	private String displayName;
	
	public Macro(Main parent, String displayName, List<Command> commands) {
		super(parent);
		this.displayName = displayName;
		this.commands = commands;
	}

	public Macro(Configurator.Context ctx) {
		this(ctx.getParent(), 
				ctx.getString(0, "MacroName"),
				ctx.getCommandList(1));
	}
	
	@Override
	public String getConfig() {
		//{Macro : ["like scene and go manual", [MANUAL, UP_ARROW]]}
		JSONQuoter quoter = new JSONQuoter(parent);
		String commandString = commands.stream().map(c -> c.name()).collect(Collectors.joining(","));
		return String.format("{%s : [%s, [%s]]}", getName(), quoter.quote(displayName), commandString);
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void onKeyPressed(KeyEvent event) {
		CommandSystem cs = parent.getCommandSystem();
		commands.forEach(c -> {
			cs.invokeCommand(c);
		});
	}
	
	public void unregister() {
		if (!parent.getCommandSystem().unregisterHandler(cmd, this)) {
			logger.warning("Unable to unregister command: " + cmd.getDisplayName());
		}
	}
	
	public void register(Command cmd) {
		this.cmd = cmd;
		//update help text
		String commandString = commands.stream().map(c -> c.name()).collect(Collectors.joining(":"));
		
		cmd.setHelpText("runs: " + commandString);
		cmd.setDisplayName(String.format("Macro[%s]", displayName));
		
		parent.getCommandSystem().registerHandler(cmd, this);
	}
}
