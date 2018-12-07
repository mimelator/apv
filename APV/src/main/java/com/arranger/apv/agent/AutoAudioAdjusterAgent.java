package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.helpers.AutoAudioAdjuster;
import com.arranger.apv.util.Configurator;

public class AutoAudioAdjusterAgent extends BaseAgent {
	
	private static final int DEFAULT_FRAMES_TO_SKIP = 50;
	private static final float DEFFAULT_LOW_CMDS_PER_SEC = .1f;
	private static final float DEFFAULT_HIGH_CMDS_PER_SEC = .5f;

	private int framesToSkip;
	private float lowCmdsPerSec, highCmdsPerSec;
	
	public AutoAudioAdjusterAgent(Main parent, int framesToSkip, float lowCmdsPerSec, float highCmdsPerSec) {
		super(parent);
		this.framesToSkip = framesToSkip;
		this.lowCmdsPerSec = lowCmdsPerSec;
		this.highCmdsPerSec = highCmdsPerSec;
		
		registerAgent(getDrawEvent(), () -> {
			if (parent.frameCount % framesToSkip == 0) {
				AutoAudioAdjuster autoAudioAdjuster = parent.getAutoAudioAdjuster();
				autoAudioAdjuster.setTargets(lowCmdsPerSec, highCmdsPerSec);
				autoAudioAdjuster.adjustToLevel();
			}
		});
	}

	public AutoAudioAdjusterAgent(Configurator.Context ctx) {
		this(ctx.getParent(), 
				ctx.getInt(0, DEFAULT_FRAMES_TO_SKIP), 
				ctx.getFloat(1, DEFFAULT_LOW_CMDS_PER_SEC), 
				ctx.getFloat(2, DEFFAULT_HIGH_CMDS_PER_SEC));
	}

	@Override
	public String getConfig() {
		//{AutoAudioAdjusterAgent : [50, .1, .5]}
		return String.format("{%s : [%s, %s, %s]}", getName(), framesToSkip, lowCmdsPerSec, highCmdsPerSec);
	}
	
	
}
