package com.arranger.apv.util.frame;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.event.APVEvent;
import com.arranger.apv.event.APVEvent.EventHandler;

public class InvokeAfterOneEvent extends APVPlugin {
	
	@FunctionalInterface
	public static interface Handler {
		void handle();
	}
	
	private EventHandler register;

	public InvokeAfterOneEvent(Main parent, APVEvent<EventHandler> event, Handler handler) {
		super(parent);
		
		register = event.register(() -> {
			handler.handle();
			event.unregister(register);
		});
	}
}
