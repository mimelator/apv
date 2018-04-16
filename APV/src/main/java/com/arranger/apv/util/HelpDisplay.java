package com.arranger.apv.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.CommandSystem;
import com.arranger.apv.Main;
import com.arranger.apv.CommandSystem.APVCommand;

public class HelpDisplay extends APVPlugin {

	public HelpDisplay(Main parent) {
		super(parent);
	}

	public void showHelp() {
		Main p = parent;
		Set<String> messages = new HashSet<String>();
		CommandSystem commandSystem = p.getCommandSystem();
		
		commandSystem.visitCommands(true, e -> {
			List<APVCommand> cmds = e.getValue();
			cmds.forEach(c -> {
				messages.add(c.getName() + ": " + c.getHelpText());
			});
		});
		commandSystem.visitCommands(false, e -> {
			List<APVCommand> cmds = e.getValue();
			cmds.forEach(c -> {
				messages.add(String.valueOf(c.getCharKey()).trim() + ": " + c.getName() + ": " + c.getHelpText());
			});
		});
		
		for (String m : commandSystem.getInterceptorHelpMessages()) {
			messages.add(m);
		}
		
		List<String> sortedMessages = new ArrayList<String>(messages);
		sortedMessages.sort(Comparator.naturalOrder());
		
		int x = p.width / 4;
		int y = p.height / 8;
		p.translate(x, y);
		p.getSettingsDisplay().drawText(new ArrayList<String>(sortedMessages));
		p.translate(-x, -y);
	}
}
