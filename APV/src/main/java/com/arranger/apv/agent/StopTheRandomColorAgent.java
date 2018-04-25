package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.color.ColorSystem;
import com.arranger.apv.color.RandomColor;
import com.arranger.apv.util.Configurator;

public class StopTheRandomColorAgent extends BaseAgent {

	private static final int DEFAULT_FRAMES_TO_SKIP = 250;
	
	private int skipped = 0;
	
	public StopTheRandomColorAgent(Main parent, int framesToSkip) {
		super(parent);
		registerAgent(parent.getDrawEvent(), () -> {
			ColorSystem cs = parent.getColor();
			if (cs instanceof RandomColor) {
				skipped++;
				if (skipped % framesToSkip == 0) {
					invokeCommand(Command.CYCLE_COLORS);
				}
			}
		});
	}
	
	public StopTheRandomColorAgent(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, DEFAULT_FRAMES_TO_SKIP));
	}

}
