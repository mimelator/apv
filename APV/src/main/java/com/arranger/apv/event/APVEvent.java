package com.arranger.apv.event;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class APVEvent<T> extends APVPlugin {
	
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
	
	public boolean unregister(T registered) {
		return listeners.remove(registered);
	}
	
	public void reset() {
		listeners.clear();
	}
	
	/**
	 * Need a temp copy of the list
	 * because it might be modified on the fly due to calls to {@link #unregister(EventHandler)}
	 */
	public void fire() {
		new ArrayList<T>(listeners).forEach(l -> ((EventHandler)l).onEvent());
	}
}
