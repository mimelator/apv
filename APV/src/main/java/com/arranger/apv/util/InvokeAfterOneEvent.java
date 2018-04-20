package com.arranger.apv.util;

import com.arranger.apv.APVEvent;
import com.arranger.apv.APVEvent.EventHandler;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class InvokeAfterOneEvent extends APVPlugin {
	
	@FunctionalInterface
	public static interface Handler {
		void handle();
	}
	
	private EventHandler register;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public InvokeAfterOneEvent(Main parent, APVEvent event, Handler handler) {
		super(parent);
		
		register = event.register(() -> {
			handler.handle();
			event.unregister(register);
		});
	}
}
