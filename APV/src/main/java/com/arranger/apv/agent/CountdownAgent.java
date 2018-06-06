package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.event.APVEvent.EventHandler;
import com.arranger.apv.systems.lite.Countdown;
import com.arranger.apv.util.draw.DrawHelper;

public class CountdownAgent extends BaseAgent {
	
	private static final int COUNTDOWN_FRAMES = 1200;
	
	public CountdownAgent(Main parent) {
		super(parent);
		registerAgent(getMarqueeEvent(), () -> new Drawer());
	}
	
	private Countdown getCountdown() {
		int countDownStart = (int)(10 * Float.parseFloat(parent.getConfigValueForFlag(Main.FLAGS.COUNTDOWN_PCT)));
		return new Countdown(parent, countDownStart);
	}
	
	private class Drawer {
		private EventHandler register;
		private Drawer() {
			int countdownFrames = (int)(COUNTDOWN_FRAMES * Float.parseFloat(parent.getConfigValueForFlag(Main.FLAGS.COUNTDOWN_PCT)));
			DrawHelper drawHelper = new DrawHelper(parent, countdownFrames, getCountdown(), () -> {});
			register = getSceneCompleteEvent().register(() -> {
				drawHelper.complete();
				getSceneCompleteEvent().unregister(register);
			});
		}
	}
}
