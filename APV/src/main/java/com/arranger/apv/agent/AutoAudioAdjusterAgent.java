package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.helpers.AutoAudioAdjuster;
import com.arranger.apv.util.Configurator;

public class AutoAudioAdjusterAgent extends BaseAgent {
	
	private static final int DEFAULT_FRAMES_TO_SKIP = 50;
	private static final float DEFFAULT_TARGET_CMDS_PER_SEC = .5f;

	private int framesToSkip;
	private float targetCmdsPerSec;
	private boolean hasSetCmdsPerSec = false;
	
	public AutoAudioAdjusterAgent(Main parent, int framesToSkip, float targetCmdsPerSec) {
		super(parent);
		this.framesToSkip = framesToSkip;
		this.targetCmdsPerSec = targetCmdsPerSec;
		
		registerAgent(getDrawEvent(), () -> {
			if (parent.frameCount % framesToSkip == 0) {
				AutoAudioAdjuster autoAudioAdjuster = parent.getAutoAudioAdjuster();
				if (!hasSetCmdsPerSec) {
					autoAudioAdjuster.setTargetCmdsPerSec(targetCmdsPerSec);
					hasSetCmdsPerSec = true;
				} else {
					this.targetCmdsPerSec = autoAudioAdjuster.getTargetCmdsPerSec(); //stay in sync
				}
				
				autoAudioAdjuster.adjustToLevel();
			}
		});
	}

	public AutoAudioAdjusterAgent(Configurator.Context ctx) {
		this(ctx.getParent(), 
				ctx.getInt(0, DEFAULT_FRAMES_TO_SKIP), 
				ctx.getFloat(1, DEFFAULT_TARGET_CMDS_PER_SEC));
	}

	@Override
	public String getConfig() {
		//{AutoAudioAdjusterAgent : [50, .5]}
		return String.format("{%s : [%s, %s]}", getName(), framesToSkip, targetCmdsPerSec);
	}
}
