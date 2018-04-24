package com.arranger.apv.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.event.APVEvent;
import com.arranger.apv.event.APVEvent.EventHandler;


public class APVCallbackHelper extends APV<APVPlugin> {
	
	private Map<APVEvent<EventHandler>, List<CallbackHandler>> handlerMap = 
			new HashMap<APVEvent<EventHandler>, List<CallbackHandler>>();
	
	public APVCallbackHelper(Main parent, Main.SYSTEM_NAMES name) {
		super(parent, name, false);
	}

	@FunctionalInterface
	public static interface Handler {
		public void handle();
	}
	
	public void registerHandler(APVEvent<EventHandler> event, Handler handler) {
		registerHandler(event, handler, 1);
	}
	
	public void registerHandler(APVEvent<EventHandler> event, Handler handler, int framesToSkip) {
		List<CallbackHandler> handlerList = handlerMap.get(event);
		if (handlerList == null) {
			handlerList = new ArrayList<CallbackHandler>();
			handlerList.add(new CallbackHandler(handler, framesToSkip));
			handlerMap.put(event, handlerList);
			
			event.register(() -> {
				if (isEnabled()) {
					handlerMap.get(event).forEach(h -> {
					if (h.checkFrameCount()) {
						h.handler.handle();
					}});
				}
			});
		} else {
			handlerList.add(new CallbackHandler(handler, framesToSkip));
		}
	}
	
	class CallbackHandler {
		private Handler handler;
		private int frameCount = 0;
		private int framesToSkip;
		
		CallbackHandler(Handler handler, int framesToSkip) {
			this.handler = handler;
			this.framesToSkip = framesToSkip;
		}
		
		boolean checkFrameCount() {
			frameCount++;
			return (frameCount % framesToSkip == 0);
		}
	}
}
