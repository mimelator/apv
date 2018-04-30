package com.arranger.apv.event;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.event.CommandInvokedEvent.APVCommandInvokedEventHandler;

public class CommandInvokedEvent extends APVEvent<APVCommandInvokedEventHandler> {
	
	private static final Logger logger = Logger.getLogger(CommandInvokedEvent.class.getName());
	
	@FunctionalInterface
	public static interface APVCommandInvokedEventHandler {
		void onCommand(Command cmd, String source);
	}

	public CommandInvokedEvent(Main parent) {
		super(parent, EventTypes.COMMAND_INVOKED);
	}
	
	public void fire(Command cmd, String source) {
		logger.fine(String.format("cmd: %s source: %s\n", cmd.getDisplayName(), source));
		List<APVCommandInvokedEventHandler> temp = new ArrayList<APVCommandInvokedEventHandler>(listeners);
		temp.forEach(l -> ((APVCommandInvokedEventHandler)l).onCommand(cmd, source));
	}
}
