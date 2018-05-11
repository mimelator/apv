package com.arranger.apv.util.frame;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.cmd.CommandSystem;

public class FrameStrober extends APVPlugin {
	
	protected int skipNFrames = 5; //This seems to be a nice slow mo rate

	public FrameStrober(Main parent) {
		super(parent);
		
		parent.getSetupEvent().register(() -> {
			CommandSystem cs = parent.getCommandSystem();
			cs.registerHandler(Command.FRAME_STROBER_STROBE_FRAMES, 
					(command, source, modifiers) -> {if (Command.isShiftDown(modifiers)) skipNFrames-- ; else skipNFrames++;});
		});
	}

	public int getSkipNFrames() {
		return skipNFrames;
	}

	public void setSkipNFrames(int skipNFrames) {
		this.skipNFrames = skipNFrames;
	}
	
	public boolean isSkippingFrames() {
		if (parent.getFrameCount() % skipNFrames == 0) {
			return false;
		} else {
			return true;
		}
	}
}
