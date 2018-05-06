package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.event.APVEvent;
import com.arranger.apv.event.APVEvent.EventHandler;
import com.arranger.apv.helpers.APVCallbackHelper;

public class APVAgent extends APVCallbackHelper {
	
	public APVAgent(Main parent) {
		super(parent, Main.SYSTEM_NAMES.AGENTS);
	}

	public void registerHandler(APVEvent<EventHandler> event, Handler handler) {
		super.registerHandler(event, handler, 1, null);
	}
	
	public void registerHandler(APVEvent<EventHandler> event, Handler handler, int framesToSkip) {
		super.registerHandler(event, handler, framesToSkip, null);
	}
	
	public void reloadConfiguration() {
		resetHandlerMap();
		initialize(parent, Main.SYSTEM_NAMES.AGENTS, false);
	}
}
