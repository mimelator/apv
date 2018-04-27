package com.arranger.apv.agent.automation;

import com.arranger.apv.Main;
import com.arranger.apv.agent.BaseAgent;
import com.arranger.apv.helpers.APVCallbackHelper.Handler;
import com.arranger.apv.util.Configurator;

public class AutomationAgent extends BaseAgent implements Handler {
	
	private AgentEvent agentEvent;
	private Conditions conditions;
	private AgentAction action;
	private String displayName;

	public AutomationAgent(Main parent, 
			String displayName, 
			AgentEvent agentEvent,
			Conditions conditions,
			AgentAction action) {
		super(parent);
		
		this.displayName = displayName;
		this.conditions = conditions;
		this.action = action;
		this.agentEvent = agentEvent;
		
		agentEvent.setHandler(this);
	}
	
	public AutomationAgent(Configurator.Context ctx) {
		this(ctx.getParent(), 
					ctx.getString(0, AutomationAgent.class.getSimpleName()),
					(AgentEvent)ctx.loadPlugin(1),
					(Conditions)ctx.loadPlugin(2),
					(AgentAction)ctx.loadPlugin(3));
	}

	@Override
	public String getConfig() {
	//	{AutomationAgent : [StrobeFilterChangeAgent
	//						{AgentEvent : PULSE, 5}	
	//	            		{Conditions : [{Condition : [true]}]}
	//	            		{Action : [COMMAND, CYCLE_FILTERS}
	//	            	]}
		return String.format("{%s : [%s, %s, %s, %s]}", getName(),
				getDisplayName(),
				agentEvent.getConfig(),
				conditions.getConfig(),
				action.getConfig());
	}

	@Override
	public void handle() {
		if (conditions.isTrue()) {
			action.doAction(this);
		}
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}
}
