package com.arranger.apv.agent.automation;

import com.arranger.apv.Main;
import com.arranger.apv.agent.BaseAgent;
import com.arranger.apv.agent.PulseAgent;
import com.arranger.apv.event.APVEvent;
import com.arranger.apv.event.EventTypes;
import com.arranger.apv.event.APVEvent.EventHandler;
import com.arranger.apv.helpers.APVCallbackHelper.Handler;
import com.arranger.apv.util.Configurator;

public class AgentEvent extends BaseAgent {

	private APVEvent<EventHandler> event;
	private int numToSkip;
	
	public AgentEvent(Main parent, APVEvent<EventHandler> event, int numToSkip) {
		super(parent);
		this.event = event;
		this.numToSkip = numToSkip;
	}
	
	public AgentEvent(Configurator.Context ctx) {
		this(ctx.getParent(), getEventSafe(ctx.getParent(), ctx, 0), ctx.getInt(1, 0));
	}
	
	@Override
	public String getConfig() {
		//{{AgentEvent : [PULSE, 7]}	
		if (event == null) {
			return String.format("{%s : [PULSE, %d]}", getName(), numToSkip);
		} else {
			return String.format("{%s : [%s, %d]}", getName(), event.getName(), numToSkip);
		}
	}
	
	public void setHandler(Handler handler) {
		if (event == null) {
			new PulseAgent(parent, numToSkip) {
				protected void onPulse() {
					handler.handle();
				}
			};
		} else {
			registerAgent(event, handler);
		}
	}
	
	/**
	 * I can't place this function into context because it leaks the logic about "PULSE:
	 * It is static because it is called from the constructor
	 */
	private static APVEvent<EventHandler> getEventSafe(Main parent, Configurator.Context ctx, int index) {
		String eventString = ctx.getString(index, null);
		if (eventString == null || "PULSE".equals(eventString)) {
			return null;
		}
		
		return parent.getEventForType(EventTypes.valueOf(eventString));
	}
}
