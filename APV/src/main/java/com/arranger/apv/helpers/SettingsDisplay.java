package com.arranger.apv.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.factory.ShapeFactory;
import com.arranger.apv.gui.APVTextFrame;
import com.arranger.apv.gui.APVTextFrame.TextSupplier;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.TextPainter;

public class SettingsDisplay extends APVPlugin implements TextSupplier {
	
	protected List<String> settingsMessages = new ArrayList<String>();
	
	public SettingsDisplay(Main parent) {
		super(parent);
	}
	
	@Override
	public List<String> getMessages() {
		return settingsMessages;
	}

	public void reset() {
		settingsMessages.clear();
		addPrimarySettingsMessages();
	}
	
	public void addSettingsMessage(String msg) {
		settingsMessages.add(msg);
	}
	
	public void debugSystem(ShapeSystem ss, String name) {
		addSettingsMessage(name +": " + ss.getDisplayName());
		ShapeFactory factory = ss.getFactory();
		if (factory != null) {
			addSettingsMessage("  --factory: " + factory.getDisplayName());
			factory.addSettingsMessages();
			addSettingsMessage("    --scale: " + factory.getScale());
		}
	}
	
	protected void addPrimarySettingsMessages() {
		Main p = parent;
		
		addSettingsMessage("---------System Settings-------");
		addSettingsMessage("Version: " + p.getVersionInfo().getVersion());
		addSettingsMessage("Frame rate: " + (int)p.frameRate);
		addSettingsMessage("Mode: " + p.getCurrentControlMode().name());
		p.getControl().addSettingsMessages();
		addSettingsMessage("Cmds/Sec: " + VideoGameHelper.decFormat.format(p.getVideoGameHelper().getCommandsPerSec()));
		addSettingsMessage("db: " + p.getAudio().getDB());
		p.getPulseListener().addSettingsMessages();
		addSettingsMessage("---------Switches-------");
		List<String> switchList = p.getSwitches().values().
				stream().map(s -> s.getDisplayName()).collect(Collectors.toList());
		switchList.sort(null);
		switchList.forEach(s -> addSettingsMessage(s));
		
		//Last Command
		Command lastCommand = p.getCommandSystem().getLastCommand();
		if (lastCommand != null) {
			addSettingsMessage("Last Command: " + lastCommand.name());
		}
		addSettingsMessage("Newest plugin: " +  p.getVideoGameHelper().getLastNewPluginName());
		
		addSettingsMessage(" ");
		addSettingsMessage("---------Live Settings-------");
	}

	public void drawSettingsMessages() {
		new TextPainter(parent).drawText(settingsMessages, SafePainter.LOCATION.UPPER_LEFT);
	}
	
	protected void createSettingsWindow() {
		new APVTextFrame(parent, 
				getName(),
				(int)(parent.width / 6),
				(int)(parent.height * .8f),
				() -> settingsMessages);
	}
}
