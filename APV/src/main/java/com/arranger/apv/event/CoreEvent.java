package com.arranger.apv.event;

import com.arranger.apv.APVEvent;
import com.arranger.apv.Main;
import com.arranger.apv.APVEvent.EventHandler;
import com.arranger.apv.event.CoreEvent.CoreListener;

public class CoreEvent extends APVEvent<CoreListener> {
	
	public static interface CoreListener extends EventHandler {}

	public CoreEvent(Main parent) {
		super(parent);
	}

}
