package com.arranger.apv.event;

import com.arranger.apv.APVEvent;
import com.arranger.apv.Main;
import com.arranger.apv.APVEvent.EventHandler;
import com.arranger.apv.event.DrawShapeEvent.DrawShapeSytemListener;

public class DrawShapeEvent extends APVEvent<DrawShapeSytemListener> {
	
	public static interface DrawShapeSytemListener extends EventHandler {}

	public DrawShapeEvent(Main parent) {
		super(parent);
	}

}
