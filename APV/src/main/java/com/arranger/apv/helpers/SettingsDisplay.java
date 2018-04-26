package com.arranger.apv.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.factory.ShapeFactory;
import com.arranger.apv.gui.APVTextFrame;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.TextPainter;

public class SettingsDisplay extends APVPlugin {
	
	private static final Logger logger = Logger.getLogger(SettingsDisplay.class.getName());
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
	
	public void debugSystem(ShapeSystem ss, String name) {
		logger.fine("Drawing system [" + name + "] [" + ss.getName() +"]");
		addSettingsMessage(name +": " + ss.getName());
		ShapeFactory factory = ss.getFactory();
		if (factory != null) {
			addSettingsMessage("  --factory: " + factory.getName());
			factory.addSettingsMessages();
			addSettingsMessage("    --scale: " + factory.getScale());
		}
	}
	
	protected void addPrimarySettingsMessages() {
		Main p = parent;
		
		addSettingsMessage("---------System Settings-------");
		addSettingsMessage("Version: " + p.getVersionInfo().getVersion());
		addSettingsMessage("Frame rate: " + (int)p.frameRate);
		addSettingsMessage("Monitoring Enabled: " + p.isMonitoringEnabled());
		addSettingsMessage("Mode: " + p.getCurrentControlMode().name());
		p.getControl().addSettingsMessages();
		addSettingsMessage("Cmds/Sec: " + VideoGameHelper.decFormat.format(p.getVideoGameHelper().getCommandsPerSec()));
		addSettingsMessage("Loc: " + p.getLocations().getPlugin().getDisplayName());
		addSettingsMessage("Color: " + p.getColor().getDisplayName());
		addSettingsMessage("Gain: " + p.getAudio().getGain());
		addSettingsMessage("ParticlePct: " + String.format("%.0f%%", parent.getParticles().getPct() * 100));
		addSettingsMessage("Num Liked Scenes: " + p.getLikedScenes().size());
		addSettingsMessage("MouseXY:  " + p.mouseX + " " + p.mouseY);
		addSettingsMessage("Transitions Frames : " + p.getTransition().getTransitionFrames());
		addSettingsMessage("FrameStrober SkipRate: " + p.getFrameStrober().getSkipNFrames());
		p.getPulseListener().addSettingsMessages();
		
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
