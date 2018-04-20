package com.arranger.apv.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.CommandSystem;
import com.arranger.apv.CommandSystem.APVCommand;
import com.arranger.apv.Main;
import com.arranger.apv.gui.APVTextFrame;

public class HelpDisplay extends APVPlugin {

	public HelpDisplay(Main parent) {
		super(parent);
		
		parent.getSetupEvent().register(() -> {
			parent.getCommandSystem().registerCommand('w', "SettingsWindow", 
				"Popup window to display Help", 
				e -> createSettingsWindow());
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
		return sortedMessages;
	}
	
	protected void createSettingsWindow() {
		new APVTextFrame(parent, 
				"help",
				(int)(parent.width / 2),
				(int)(parent.height * .8f),
				parent.getDrawEvent(),
				() -> getMessages());
	}
}
