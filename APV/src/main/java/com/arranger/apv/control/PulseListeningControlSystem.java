package com.arranger.apv.control;

import com.arranger.apv.Main;
import com.arranger.apv.audio.PulseListener;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem;

public abstract class PulseListeningControlSystem extends ControlSystem {
	
	protected PulseListener autoSkipPulseListener;

	public PulseListeningControlSystem(Main parent) {
		super(parent);
		
		autoSkipPulseListener = new PulseListener(parent, getDefaultPulsesToSkip());
		
		parent.getSetupEvent().register(() -> {
			CommandSystem cs = parent.getCommandSystem();
			cs.registerHandler(Command.PULSE_SKIP_INC, (cmd, src, mod) -> onCommand(cmd));
			cs.registerHandler(Command.PULSE_SKIP_DEC, (cmd, src, mod) -> onCommand(cmd));
		});
	}

	protected abstract int getDefaultPulsesToSkip();
	protected abstract Command _getNextCommand();
	
	protected void onCommand(Command command) {
		int offset = 1;
		String arg = command.getArgument();
		if (arg != null) {
			offset = Integer.parseInt(arg);
		}
		command.setArgument(null);
		
		if (command.equals(Command.PULSE_SKIP_DEC)) {
			offset = -offset;
		}
		
		autoSkipPulseListener.adjustPulsesToSkip(offset);
	}

	@Override
	public boolean allowsMouseLocation() {
		return false;
	}
	
	@Override
	public void addSettingsMessages() {
		super.addSettingsMessages();
		autoSkipPulseListener.addSettingsMessages();
	}
	
	@Override
	public Command getNextCommand() {
		if (autoSkipPulseListener.isNewPulse()) {
			return _getNextCommand();
		} else {
			return null;
		}
	}
}
