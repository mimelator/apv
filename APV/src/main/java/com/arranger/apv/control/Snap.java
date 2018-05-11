package com.arranger.apv.control;

import com.arranger.apv.Main;
import com.arranger.apv.audio.SnapListener;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem;

public class Snap extends ControlSystem {
	
	private static final int DEFAULT_FRAMES_TO_SKIP_FOR_SNAP = 100; 
	
	private SnapListener snapListener;

	public Snap(Main parent) {
		super(parent);
		snapListener = new SnapListener(parent, DEFAULT_FRAMES_TO_SKIP_FOR_SNAP);
		
		parent.getSetupEvent().register(() -> {
			CommandSystem cs = parent.getCommandSystem();
			cs.registerHandler(Command.QUIET_WINDOW_LENGTH_INC,
					(command, source, modifiers) -> snapListener.incrementFramesToSkip());
			cs.registerHandler(Command.QUIET_WINDOW_LENGTH_DEC,
					(command, source, modifiers) -> snapListener.deccrementFramesToSkip());
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
	public Command getNextCommand() {
		if (snapListener.isSnap()) {
			return Command.SCRAMBLE;
		} else {
			return null;
		}
	}
}
