package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.CommandSystem;
import com.arranger.apv.Main;

public class FrameStrober extends APVPlugin {
	
	protected int skipNFrames = 5; //This seems to be a nice slow mo rate

	public FrameStrober(Main parent) {
		super(parent);
		
		parent.registerSetupListener(() -> {
			CommandSystem cs = parent.getCommandSystem();
			cs.registerCommand('a', "SkipFrame Rate", "Increases/Decreases the frames to skip (hold shift to decrease)", 
					event -> {if (event.isShiftDown()) skipNFrames-- ; else skipNFrames++;});
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
