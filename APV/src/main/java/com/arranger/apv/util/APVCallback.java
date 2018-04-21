package com.arranger.apv.util;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;


public class APVCallback extends APV<APVPlugin> {
	
	private List<CallbackHandler> handlers = new ArrayList<CallbackHandler>();
	
	public APVCallback(Main parent, String name) {
		super(parent, name, false);
		
		parent.getDrawEvent().register(() -> {
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
