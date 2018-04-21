package com.arranger.apv;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVEvent.EventHandler;

public class APVEvent<T extends EventHandler> extends APVPlugin {
	
	@FunctionalInterface
	public static interface EventHandler {
		void onEvent();
	}

	protected List<T> listeners = new ArrayList<T>();
	
	public APVEvent(Main parent) {
		super(parent);
	}
	
	public T register(T listener) {
		listeners.add(listener);
		return listener;
	}
	
	public void unregister(T registered) {
		listeners.remove(registered);
	}
	
	/**
	 * Need a temp copy of the list
	 * because it might be modified on the fly due to calls to {@link #unregister(EventHandler)}
	 */
	public void fire() {
		new ArrayList<T>(listeners).forEach(l -> l.onEvent());
	}
}
