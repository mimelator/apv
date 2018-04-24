package com.arranger.apv.event;

import com.arranger.apv.Main;
import com.arranger.apv.event.APVEvent.EventHandler;

public class CoreEvent extends APVEvent<EventHandler> {
	
	public CoreEvent(Main parent) {
		super(parent);
	}

}
