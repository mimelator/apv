package com.arranger.apv.event;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVEvent;
import com.arranger.apv.Command;
import com.arranger.apv.Main;
import com.arranger.apv.event.CommandInvokedEvent.APVCommandInvokedEventHandler;

public class CommandInvokedEvent extends APVEvent<APVCommandInvokedEventHandler> {
	
	@FunctionalInterface
	public static interface APVCommandInvokedEventHandler {
		void onCommand(Command cmd);
	}

	public CommandInvokedEvent(Main parent) {
		super(parent);
	}
	
	public void fire(Command cmd) {
		List<APVCommandInvokedEventHandler> temp = new ArrayList<APVCommandInvokedEventHandler>(listeners);
		temp.forEach(l -> ((APVCommandInvokedEventHandler)l).onCommand(cmd));
	}
}
