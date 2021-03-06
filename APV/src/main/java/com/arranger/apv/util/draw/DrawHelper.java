package com.arranger.apv.util.draw;

import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.util.frame.FrameFader;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.event.APVEvent.EventHandler;

public class DrawHelper extends APVPlugin {
	
	private static final int DEFAULT_FRAMES_TO_DRAW = 30;
	
	private EventHandler eventHandler;
	private FrameFader frameFader;
	
	@FunctionalInterface
	public static interface DrawCompleteHandler {
		void onDrawComplete();
	}
	
	public DrawHelper(Main parent, ShapeSystem ss, DrawCompleteHandler handler) {
		this(parent, DEFAULT_FRAMES_TO_DRAW, ss, handler);
	}
	
	public DrawHelper(Main parent, int numFramesToDraw, ShapeSystem ss, DrawCompleteHandler handler) {
		super(parent);

		frameFader = new FrameFader(parent, numFramesToDraw);
		frameFader.startFade();
		
		//Keep drawing until the fader is no longer active
		eventHandler = parent.getDrawEvent().register(() -> {
			if (!frameFader.isFadeActive()) {
				parent.getDrawEvent().unregister(eventHandler);
				eventHandler = null;
				frameFader = null;
				handler.onDrawComplete();
			} else {
				parent.drawSystem(ss, ss.getDisplayName());
			}
		});
	}
	
	public void complete() {
		if (frameFader != null) {
			frameFader.stopFade();
		}
	}
}
