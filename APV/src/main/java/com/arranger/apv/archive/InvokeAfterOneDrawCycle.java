package com.arranger.apv.archive;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.event.CoreEvent;
import com.arranger.apv.event.CoreEvent.CoreListener;

public class InvokeAfterOneDrawCycle extends APVPlugin {
	
	@FunctionalInterface
	public static interface Handler {
		void handle();
	}
	
	private CoreListener listener;

	public InvokeAfterOneDrawCycle(Main parent, Handler handler) {
		super(parent);
		
		CoreEvent drawEvent = parent.getDrawEvent();
		
		listener = drawEvent.register(() -> {
			handler.handle();
			drawEvent.unregister(listener);
		});
		
	}
}
