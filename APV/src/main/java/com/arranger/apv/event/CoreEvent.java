package com.arranger.apv.event;

import com.arranger.apv.APVEvent;
import com.arranger.apv.APVEvent.EventHandler;
import com.arranger.apv.Main;

public class CoreEvent extends APVEvent<EventHandler> {
	
	public CoreEvent(Main parent) {
		super(parent);
	}

}
