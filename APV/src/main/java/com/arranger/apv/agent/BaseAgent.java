package com.arranger.apv.agent;

import java.util.Arrays;
import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.Main.FLAGS;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.event.APVEvent;
import com.arranger.apv.event.APVEvent.EventHandler;
import com.arranger.apv.helpers.APVCallbackHelper.Handler;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.TextDrawHelper;

public class BaseAgent extends APVPlugin {
	
	private static Logger logger = Logger.getLogger(BaseAgent.class.getName());
	private boolean debugMessageEnabled;

	public BaseAgent(Main parent) {
		super(parent);
		debugMessageEnabled = parent.getConfigBooleanForFlag(FLAGS.DEBUG_AGENT_MESSAGES);
	}

	protected void registerAgent(APVEvent<EventHandler> event, Handler handler) {
		parent.getSetupEvent().register(() -> {
			parent.getAgent().registerHandler(event, handler);
		});
	}
	
	public void invokeCommand(Command command) {
		parent.getCommandSystem().invokeCommand(command, getDisplayName(), 0);
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
	
	public APVEvent<EventHandler> getKScopeEvent() {
		return parent.getKScopeEvent();
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
	
	public APVEvent<EventHandler> getALDAEvent() {
		return parent.getALDAEvent();
	}
	
	public void fireDebugMessage() {
		fireDebugMessage(getDisplayName());
	}
	
	public void fireDebugMessage(String msg) {
		if (debugMessageEnabled) {
			parent.sendMessage(new String[] {msg});
			
			new TextDrawHelper(parent, 10, Arrays.asList(new String[] {msg}), SafePainter.LOCATION.LOWER_RIGHT);
		}
	}
}
