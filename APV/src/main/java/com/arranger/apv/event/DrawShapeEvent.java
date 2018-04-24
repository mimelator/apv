package com.arranger.apv.event;

import com.arranger.apv.Main;
import com.arranger.apv.event.APVEvent.EventHandler;

public class DrawShapeEvent extends APVEvent<EventHandler> {
	
	public DrawShapeEvent(Main parent) {
		super(parent);
	}

}
