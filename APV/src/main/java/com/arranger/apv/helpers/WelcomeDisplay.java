package com.arranger.apv.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem.RegisteredCommandHandler;

import edu.emory.mathcs.backport.java.util.Arrays;

public class WelcomeDisplay extends HelpDisplay {
	
	@SuppressWarnings("unchecked")
	private static final List<Command> WELCOME_COMMANDS = Arrays.asList(
			new Command[] {
			Command.SWITCH_HELP,
			Command.SCRAMBLE,
			Command.SWITCH_WELCOME,
			Command.HOT_KEY_1,
			Command.RANDOMIZE_COLORS
			});
	

	public WelcomeDisplay(Main parent) {
		super(parent);
	}
	
	
	public List<String> getMessages() {
		Main p = parent;
		Set<String> messages = new HashSet<String>();
		Map<Command, List<RegisteredCommandHandler>> commands = p.getCommandSystem().getCommands();
		
		WELCOME_COMMANDS.forEach(wc -> {
			List<RegisteredCommandHandler> cmds = commands.get(wc);
			cmds.forEach(c -> {
				String key = "";
				Command cmd = c.getCommand();
				if (cmd.hasCharKey()) {
					if (cmd.getModifiers() != 0) {
						key = "Ctrl+" + cmd.getCharKey();
					} else {
						key = wc.getKey();
						if (key.charAt(0) == '\n') {
							key = "";
						}
					}
				} else {
					key = cmd.name();
				}
				
				String msg = String.format("[%s]  %s: %s", 
						key, 
						cmd.getDisplayName(), 
						c.getHelpText());
				messages.add(msg);
			});
		});
		
		return new ArrayList<String>(messages);
	}

}
