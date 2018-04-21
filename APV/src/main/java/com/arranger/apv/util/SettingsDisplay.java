package com.arranger.apv.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Command;
import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;
import com.arranger.apv.ShapeSystem;
import com.arranger.apv.Switch;
import com.arranger.apv.gui.APVTextFrame;

import processing.core.PApplet;

public class SettingsDisplay extends APVPlugin {
	
	private static final Logger logger = Logger.getLogger(SettingsDisplay.class.getName());
	
	public static final int TEXT_SIZE = 16;
	public static final int TEXT_OFFSET = 10;
	protected List<String> settingsMessages = new ArrayList<String>();
	
	public SettingsDisplay(Main parent) {
		super(parent);
		
		parent.getSetupEvent().register(() -> {
				parent.getCommandSystem().registerHandler(Command.WINDOWS, 
					e -> createSettingsWindow());
		});
	}
	
	public void reset() {
		settingsMessages.clear();
		addPrimarySettingsMessages();
	}
	
	public void addSettingsMessage(String msg) {
		settingsMessages.add(msg);
	}
	
	public void drawText(List<String> msgs) {
		new SafePainter(parent, () -> {
			parent.fill(255);
			parent.textAlign(PApplet.LEFT, PApplet.TOP);
			parent.textSize(TEXT_SIZE);
			
			int offset = TEXT_OFFSET;
			for (String s : msgs) {
				parent.text(s, TEXT_OFFSET, offset);
				offset += TEXT_SIZE;
			}
		}).paint();
	}
	
	public void debugSystem(ShapeSystem ss, String name) {
		logger.fine("Drawing system [" + name + "] [" + ss.getName() +"]");
		addSettingsMessage(name +": " + ss.getName());
		ShapeFactory factory = ss.getFactory();
		if (factory != null) {
			addSettingsMessage("  --factory: " + factory.getName());
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
		addSettingsMessage("Loc: " + p.getLocations().getPlugin().getDisplayName());
		addSettingsMessage("Color: " + p.getColor().getDisplayName());
		addSettingsMessage("Audio: " + p.getAudio().getScaleFactor());
		addSettingsMessage("FrameStrober SkipRate: " + p.getFrameStrober().getSkipNFrames());
		addSettingsMessage("ParticlePct: " + String.format("%.0f%%", parent.getParticles().getPct() * 100));
		addSettingsMessage("MouseXY:  " + p.mouseX + " " + p.mouseY);
		addSettingsMessage("Transitions Frames : " + p.getTransition().getTransitionFrames());
		p.getPulseListener().addSettingsMessages();
		
		for (Switch s : p.getSwitches()) {
			addSettingsMessage(s.getDisplayName());
		}
		
		//Last Command
		Command lastCommand = p.getCommandSystem().getLastCommand();
		if (lastCommand != null) {
			addSettingsMessage("Last Command: " + lastCommand.name());
		}
		
		addSettingsMessage(" ");
		addSettingsMessage("---------Live Settings-------");
	}

	public void drawSettingsMessages() {
		drawText(settingsMessages);
	}
	
	protected void createSettingsWindow() {
		new APVTextFrame(parent, 
				"settings",
				(int)(parent.width / 6),
				(int)(parent.height * .8f),
				parent.getDrawEvent(),
				() -> settingsMessages);
	}
}
