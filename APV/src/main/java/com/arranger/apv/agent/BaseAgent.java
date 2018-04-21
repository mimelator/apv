package com.arranger.apv.agent;

import org.testng.log4testng.Logger;

import com.arranger.apv.APVEvent;
import com.arranger.apv.APVEvent.EventHandler;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.APVCallback.Handler;

public class BaseAgent extends APVPlugin {
	
	private static Logger logger = Logger.getLogger(BaseAgent.class);

	public BaseAgent(Main parent) {
		super(parent);
	}

	protected void registerAgent(APVEvent<EventHandler> event, Handler handler) {
		//I might need to regsister for a later time, but we'll see
		event.register(() -> {
			parent.getAgent().registerHandler(handler);
		});
	}
	
	protected void invokeCommand(char c) {
		parent.getCommandSystem().invokeCommand(c);
		logger.info(String.format("%1s invoked cmd: %2s\n", getName(), c));
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
}
