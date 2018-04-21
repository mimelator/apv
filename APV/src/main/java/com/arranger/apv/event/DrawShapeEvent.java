package com.arranger.apv.event;

import com.arranger.apv.APVEvent;
import com.arranger.apv.APVEvent.EventHandler;
import com.arranger.apv.Main;

public class DrawShapeEvent extends APVEvent<EventHandler> {
	
	public DrawShapeEvent(Main parent) {
		super(parent);
	}

}
