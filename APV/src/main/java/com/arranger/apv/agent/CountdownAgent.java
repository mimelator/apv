package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.systems.lite.Countdown;
import com.arranger.apv.util.draw.DrawHelper;

public class CountdownAgent extends BaseAgent {
	
	private static final int COUNTDOWN_FRAMES = 1200;
	
	private DrawHelper drawHelper;

	public CountdownAgent(Main parent) {
		super(parent);
		
		registerAgent(getMarqueeEvent(), () -> {
			if (drawHelper == null) {
				drawHelper = new DrawHelper(parent, COUNTDOWN_FRAMES, getCountdown(), () -> drawHelper = null);
			}
		});
		
		registerAgent(getSceneCompleteEvent(), () -> {
			if (drawHelper != null) {
				drawHelper.complete();
				drawHelper = null;
			}
		});
	}
	
	private Countdown getCountdown() {
		return new Countdown(parent, 10);
	}
}
