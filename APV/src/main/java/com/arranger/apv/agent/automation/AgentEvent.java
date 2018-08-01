package com.arranger.apv.agent.automation;

import com.arranger.apv.Main;
import com.arranger.apv.agent.BaseAgent;
import com.arranger.apv.agent.PulseAgent;
import com.arranger.apv.event.APVEvent;
import com.arranger.apv.event.APVEvent.EventHandler;
import com.arranger.apv.event.EventTypes;
import com.arranger.apv.helpers.APVCallbackHelper.Handler;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.frame.Oscillator;

/**
 * TODO: Must be able to configure the Cycle Time
 * @author markimel
 *
 */
public class AgentEvent extends BaseAgent implements Handler {

	private static final int DEFAULT_CYCLE_TIME = 1000;
	
	private APVEvent<EventHandler> event;
	private int numToSkip;
	private int optionalHighNumToSkip;
	private int skipped = 0;
	private Handler handler;
	private Oscillator oscillator;
	
	public AgentEvent(Main parent, APVEvent<EventHandler> event, int numToSkip, int optionalHighNumToSkip) {
		super(parent);
		this.event = event;
		this.numToSkip = numToSkip;
		this.optionalHighNumToSkip = optionalHighNumToSkip;
		
		if (optionalHighNumToSkip > 0) {
			oscillator = new Oscillator(parent);
		}
	}
	
	public AgentEvent(Configurator.Context ctx) {
		this(ctx.getParent(), 
				getEventSafe(ctx.getParent(), ctx, 0), 
				ctx.getInt(1, 0),
				ctx.getInt(2, 0));
	}
	
	@Override
	public String getConfig() {
		//{{AgentEvent : [PULSE, 7]}	
		if (event == null) {
			return String.format("{%s : [PULSE, %d]}", getName(), numToSkip);
		} else if (optionalHighNumToSkip == 0){
			return String.format("{%s : [%s, %d]}", getName(), event.getEventType().name(), numToSkip);
		} else {
			return String.format("{%s : [%s, %s, %s]}", getName(), event.getEventType().name(), numToSkip, optionalHighNumToSkip);
		}
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
		if (event == null) {
			new PulseAgent(parent, 0) {
				protected void onPulse() {
					handle();
				}
			};
		} else {
			registerAgent(event, this);
		}
	}
	
	@Override
	public void handle() {
		skipped++;
		boolean shouldHandle = false;
		
		//skip N, then invoke
		if (oscillator != null) {
			float oscillate = oscillator.oscillate(numToSkip, optionalHighNumToSkip, DEFAULT_CYCLE_TIME);
			System.out.printf("oscillate%s: numToSkip:%s optionalHighNumToSkip:%s\n", oscillate, numToSkip, optionalHighNumToSkip);
			if (skipped > oscillate) {
				shouldHandle = true;
			}
		} else if (skipped >= numToSkip) {
			//no oscillator
			shouldHandle = true;
		}
		
		if (shouldHandle) {
			handler.handle();
			skipped = 0;
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
