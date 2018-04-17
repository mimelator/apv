package com.arranger.apv.control;

import com.arranger.apv.CommandSystem;
import com.arranger.apv.ControlSystem;
import com.arranger.apv.Main;
import com.arranger.apv.audio.SnapListener;

import processing.event.KeyEvent;

public class Snap extends ControlSystem {
	
	private static final int DEFAULT_FRAMES_TO_SKIP_FOR_SNAP = 100; 
	
	private SnapListener snapListener;

	public Snap(Main parent) {
		super(parent);
		snapListener = new SnapListener(parent, DEFAULT_FRAMES_TO_SKIP_FOR_SNAP);
		
		parent.registerSetupListener(() -> {
			CommandSystem cs = parent.getCommandSystem();
			cs.registerCommand(')', "Frame++", "Increases the size of the Quiet Window in Snap mode",
					event -> snapListener.incrementFramesToSkip());
			cs.registerCommand('(', "Frame--", "Decreases the size of the Quiet Window in Snap mode",
					event -> snapListener.deccrementFramesToSkip());
		});
	}

	@Override
	public CONTROL_MODES getControlMode() {
		return CONTROL_MODES.SNAP;
	}
	
	@Override
	public void addSettingsMessages() {
		parent.addSettingsMessage("   ---Quiet Frames: " + snapListener.getFramesToSkip());
	}

	@Override
	public KeyEvent getNextCommand() {
		if (snapListener.isSnap()) {
			return keyEventHelper.createScramble();
		} else {
			return null;
		}
	}

}
