package com.arranger.apv.agent;

import org.testng.log4testng.Logger;

import com.arranger.apv.APVEvent;
import com.arranger.apv.APVEvent.EventHandler;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Command;
import com.arranger.apv.Main;
import com.arranger.apv.util.APVCallbackHelper.Handler;

public class BaseAgent extends APVPlugin {
	
	private static Logger logger = Logger.getLogger(BaseAgent.class);

	public BaseAgent(Main parent) {
		super(parent);
	}

	protected void registerAgent(APVEvent<EventHandler> event, Handler handler) {
		parent.getSetupEvent().register(() -> {
			parent.getAgent().registerHandler(event, handler);
		});
	}
	
	protected void invokeCommand(Command command) {
		parent.getCommandSystem().invokeCommand(command);
		logger.info(String.format("%1s invoked cmd: %2s\n", getName(), command.name()));
		//System.out.printf("%1s invoked cmd: %2s\n", getName(), command.name());
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
