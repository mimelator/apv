package com.arranger.apv.helpers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem.RegisteredCommandHandler;
import com.arranger.apv.gui.APVTextFrame;
import com.arranger.apv.gui.APVTextFrame.TextSupplier;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.TextPainter;

public class HelpDisplay extends APVPlugin implements TextSupplier {

	public HelpDisplay(Main parent) {
		super(parent);
	}

	public void showHelp() {
		List<String> sortedMessages = getMessages();
		
		new SafePainter(parent, () -> {
			Main p = parent;
			int x = p.width / 3;
			int y = p.height / 12;
			p.translate(x, y);
			new TextPainter(parent).drawText(new ArrayList<String>(sortedMessages));
			p.translate(-x, -y);
		}).paint();
	}

	public List<String> getMessages() {
		Main p = parent;
		Set<String> messages = new HashSet<String>();
		
		p.getCommandSystem().getCommands().entrySet().forEach(e -> {
			List<RegisteredCommandHandler> cmds = e.getValue();
			cmds.forEach(c -> {
				String key = "";
				Command cmd = c.getCommand();
				if (cmd.hasCharKey()) {
					if (cmd.getModifiers() != 0) {
						key = "Ctrl+" + cmd.getCharKey();
					} else {
						key = e.getKey().getKey();
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
		
		List<String> sortedMessages = new ArrayList<String>(messages);
		sortedMessages.sort(Comparator.naturalOrder());
		return sortedMessages;
	}
	
	protected void createHelpWindow() {
		new APVTextFrame(parent, 
				getName(),
				(int)(parent.width / 2),
				(int)(parent.height * .8f),
				() -> getMessages());
	}
}
