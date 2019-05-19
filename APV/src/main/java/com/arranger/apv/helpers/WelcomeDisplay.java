package com.arranger.apv.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem.RegisteredCommandHandler;
import com.arranger.apv.util.draw.SafePainter;

import edu.emory.mathcs.backport.java.util.Arrays;

public class WelcomeDisplay extends HelpDisplay {
	
	public static final String DIRECTIONS = "Welcome to Wavelength\n" + 
			"  Press 'm' to access the menu and 'e' to dismiss this message\n" + 
			"  You can also access many other commands like the following:\n";
	
	private List<String> messages;
	
	@SuppressWarnings("unchecked")
	private static final List<Command> WELCOME_COMMANDS = Arrays.asList(
			new Command[] {
			Command.SWITCH_HELP,
			Command.SCRAMBLE,
			Command.SWITCH_WELCOME,
			Command.HOT_KEY_1,
			Command.RANDOMIZE_COLORS,
			});
	

	public WelcomeDisplay(Main parent) {
		super(parent);
	}
	
	@Override
	public void showHelp() {
		//Directions
		String msg = getMessages().stream().collect(Collectors.joining("\n"));
		
		new SafePainter(parent, ()-> {
			parent.textAlign(CENTER);
			parent.textSize(parent.getGraphics().textSize * .75f);
			parent.text(msg, parent.width / 2, parent.height * .15f);
		}).paint();
	}

	public List<String> getMessages() {
		if (messages == null) {
			messages = createMessages();
		}
		
		return messages;
	}

	private List<String> createMessages() {
		Main p = parent;
		Set<String> cmdMessages = new HashSet<String>();
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
				cmdMessages.add(msg);
			});
		});
		
		ArrayList<String> msgs = new ArrayList<String>();
		msgs.add(DIRECTIONS);
		msgs.addAll(cmdMessages);
		msgs.add("APV Version: " + parent.getVersionInfo().getVersion());
		return msgs;
	}
}
