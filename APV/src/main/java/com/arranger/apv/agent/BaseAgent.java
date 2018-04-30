package com.arranger.apv.agent;

import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.event.APVEvent;
import com.arranger.apv.event.APVEvent.EventHandler;
import com.arranger.apv.helpers.APVCallbackHelper.Handler;

public class BaseAgent extends APVPlugin {
	
	private static Logger logger = Logger.getLogger(BaseAgent.class.getName());

	public BaseAgent(Main parent) {
		super(parent);
	}

	protected void registerAgent(APVEvent<EventHandler> event, Handler handler) {
		parent.getSetupEvent().register(() -> {
			parent.getAgent().registerHandler(event, handler);
		});
	}
	
	public void invokeCommand(Command command) {
		parent.getCommandSystem().invokeCommand(command, getDisplayName());
		logger.info(String.format("%s invoked cmd: %s\n", getDisplayName(), command.name()));
	}
	
	protected APVEvent<EventHandler> getSceneCompleteEvent() {
		return parent.getSceneCompleteEvent();
	}
	
	public APVEvent<EventHandler> getSetupEvent() {
		return parent.getSetupEvent();
	}
	
	public APVEvent<EventHandler> getDrawEvent() {
		return parent.getDrawEvent();
	}
	
	public APVEvent<EventHandler> getSparkEvent() {
		return parent.getSparkEvent();
	}
	
	public APVEvent<EventHandler> getStrobeEvent() {
		return parent.getStrobeEvent();
	}
	
	public APVEvent<EventHandler> getCarnivalEvent() {
		return parent.getCarnivalEvent();
	}
	
	public APVEvent<EventHandler> getTwirlEvent() {
		return parent.getTwirlEvent();
	}
	
	public APVEvent<EventHandler> getMarqueeEvent() {
		return parent.getMarqueeEvent();
	}
}
