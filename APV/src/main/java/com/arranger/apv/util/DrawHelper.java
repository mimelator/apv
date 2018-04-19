package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.ShapeSystem;
import com.arranger.apv.event.CoreEvent.CoreListener;

public class DrawHelper extends APVPlugin {
	
	private static final int DEFAULT_FRAMES_TO_DRAW = 30;
	
	private CoreListener listener;
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
		
		listener = parent.getDrawEvent().register(() -> {
			if (!frameFader.isFadeActive()) {
				parent.getDrawEvent().unregister(listener);
				listener = null;
				frameFader = null;
				handler.onDrawComplete();
			} else {
				parent.drawSystem(ss, ss.getDisplayName());
			}
		});
	}
}
