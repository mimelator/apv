package com.arranger.apv.agent;

import com.arranger.apv.Command;
import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class StopTheTwirlAgent extends BaseAgent {

	private static final int DEFAULT_MAX_OSC_TO_SKIP = 4;
	
	int skipCount;
	int maxOscillationsToSkip;
	int currentOscToSkip;
	
	public StopTheTwirlAgent(Main parent, int maxOscillationsToSkip) {
		super(parent);
		this.maxOscillationsToSkip = maxOscillationsToSkip;
		currentOscToSkip = getCurrentOscToSkip(maxOscillationsToSkip);
		
		registerAgent(getTwirlEvent(), () -> {
			skipCount++;
			if (skipCount % maxOscillationsToSkip == 0) {
				invokeCommand(Command.CYCLE_BACKDROPS);
				this.currentOscToSkip = getCurrentOscToSkip(maxOscillationsToSkip);
				skipCount = 0;
			}
		});
	}
	
	public StopTheTwirlAgent(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, DEFAULT_MAX_OSC_TO_SKIP));
	}

	@Override
	public String getConfig() {
		//{StopTheTwirlAgent : [8]}
		return String.format("{%s : [%d]}", getName(), maxOscillationsToSkip);
	}
	
	private int getCurrentOscToSkip(int maxOscillationsToSkip) {
		int result = (int)parent.random(1, maxOscillationsToSkip);
		return result * 2; 		//need a full oscillation cycle
	}
}
