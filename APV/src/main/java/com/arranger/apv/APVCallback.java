package com.arranger.apv;

import java.util.ArrayList;
import java.util.List;


public class APVCallback extends APV<APVPlugin> {
	
	private List<CallbackHandler> handlers = new ArrayList<CallbackHandler>();

	public APVCallback(Main parent, String name) {
		super(parent, name, false);
		
		parent.registerDrawListener(() -> {
			if (isEnabled()) {
				handlers.forEach(h -> {
					if (h.shouldHandle()) {
						h.handler.handle();
					}
				});
			}
		});
	}

	@FunctionalInterface
	public static interface Handler {
		public void handle();
	}
	
	public void registerHandler(Handler handler) {
		registerHandler(handler, 1);
	}
	
	public void registerHandler(Handler handler, int framesToSkip) {
		handlers.add(new CallbackHandler(handler, framesToSkip));
	}
	
	class CallbackHandler {
		private Handler handler;
		private int frameCount = 0;
		private int framesToSkip;
		
		CallbackHandler(Handler handler, int framesToSkip) {
			this.handler = handler;
			this.framesToSkip = framesToSkip;
		}
		
		boolean shouldHandle() {
			frameCount++;
			
			if (frameCount % framesToSkip == 0) {
				return true;
			} else {
				return false;
			}
		}
	}
}
