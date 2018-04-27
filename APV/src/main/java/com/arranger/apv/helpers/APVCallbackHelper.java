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
	
	protected void registerHandler(APVEvent<EventHandler> event, Handler handler, APVPlugin pluginToCheck) {
		registerHandler(event, handler, 1, pluginToCheck);
	}
	
	protected void registerHandler(APVEvent<EventHandler> event, Handler handler, int framesToSkip, APVPlugin pluginToCheck) {
		List<CallbackHandler> handlerList = handlerMap.get(event);
		if (handlerList == null) {
			handlerList = new ArrayList<CallbackHandler>();
			handlerList.add(new CallbackHandler(handler, framesToSkip, pluginToCheck));
			handlerMap.put(event, handlerList);
			
			event.register(() -> {
				if (isEnabled()) {
					handlerMap.get(event).forEach(h -> {
					if (h.readyToHandle()) {
						h.handler.handle();
					}});
				}
			});
		} else {
			handlerList.add(new CallbackHandler(handler, framesToSkip, pluginToCheck));
		}
	}
	
	class CallbackHandler {
		private Handler handler;
		private int frameCount = 0;
		private int framesToSkip;
		private APVPlugin pluginToCheck;
		
		CallbackHandler(Handler handler, int framesToSkip, APVPlugin pluginToCheck) {
			this.handler = handler;
			this.framesToSkip = framesToSkip;
			this.pluginToCheck = pluginToCheck;
		}
		
		boolean readyToHandle() {
			frameCount++;
			boolean result = (frameCount % framesToSkip == 0);
			
			if (pluginToCheck != null && result) {
				result = parent.getCurrentScene().getComponentsToDrawScene().contains(pluginToCheck);
			}
			
			return result;
		}
	}
}
