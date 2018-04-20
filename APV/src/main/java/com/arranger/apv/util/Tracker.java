package com.arranger.apv.util;

import java.util.function.Predicate;

import com.arranger.apv.APVEvent;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.APVEvent.EventHandler;

public class Tracker<T> extends APVPlugin {
	
	private APVEvent<? extends EventHandler> event;
	private boolean active = false;
	
	public Tracker(Main parent, APVEvent<? extends EventHandler> event) {
		super(parent);
		this.event = event;
	}

	public boolean isActive(Predicate<T> pred) {
		if (active) {
			return true;
		} else if (pred.test(null)) {
			active = true;
		}
		return active;
	}
	
	public void fireEvent() {
		if (event != null && active) {
			event.fire();
			event = null;
		}
	}
}
