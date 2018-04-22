package com.arranger.apv.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Command;
import com.arranger.apv.CommandSystem.RegisteredCommandHandler;
import com.arranger.apv.Main;
import com.arranger.apv.gui.APVTextFrame;

public class HelpDisplay extends APVPlugin {

	public HelpDisplay(Main parent) {
		super(parent);
		
		parent.getSetupEvent().register(() -> {
			parent.getCommandSystem().registerHandler(Command.WINDOWS, 
				e -> createHelpWindow());
		});
	}

	public void showHelp() {
		List<String> sortedMessages = getMessages();
		
		new SafePainter(parent, () -> {
			Main p = parent;
			int x = p.width / 4;
			int y = p.height / 12;
			p.translate(x, y);
			p.getSettingsDisplay().drawText(new ArrayList<String>(sortedMessages));
			p.translate(-x, -y);
		}).paint();

	}

	protected List<String> getMessages() {
		Main p = parent;
		Set<String> messages = new HashSet<String>();
		
		p.getCommandSystem().getCommands().entrySet().forEach(e -> {
			List<RegisteredCommandHandler> cmds = e.getValue();
			cmds.forEach(c -> {
				String key = e.getKey();
				if (key.length() > 1) {
					//probably a number
					key = c.getCommand().name();
				} else {
					if (key.charAt(0) == '\n') {
						key = "";
					}
				}
				
				String msg = String.format("[%1s]  %2s: %3s", 
						key, 
						c.getCommand().getDisplayName(), 
						c.getHelpText());
				messages.add(msg);
			});
		});
		
		for (String m : p.getCommandSystem().getInterceptorHelpMessages()) {
			messages.add(m);
		}
		
		List<String> sortedMessages = new ArrayList<String>(messages);
		sortedMessages.sort(Comparator.naturalOrder());
		return sortedMessages;
	}
	
	protected void createHelpWindow() {
		new APVTextFrame(parent, 
				"help",
				(int)(parent.width / 2),
				(int)(parent.height * .8f),
				parent.getDrawEvent(),
				() -> getMessages());
	}
}
