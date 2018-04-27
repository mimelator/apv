package com.arranger.apv.agent.automation;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.event.EventTypes;
import com.arranger.apv.util.Configurator;

public class AgentAction extends APVPlugin {
	
	public static enum ACTION {COMMAND, FIRE, ACTIVATE};

	private ACTION action;
	private String val1;
	private String val2;
	
	public AgentAction(Main parent, ACTION action, String val1, String val2) {
		super(parent);
		this.action = action;
		this.val1 = val1;
		this.val2 = val2;
	}
	
	public AgentAction(Configurator.Context ctx) {
		this(ctx.getParent(), 
				ACTION.valueOf(ctx.getString(0, ACTION.COMMAND.name())),
				ctx.getString(1, null),
				ctx.getString(2, null));
	}

	@Override
	public String getConfig() {
		//{Action : [FIRE, CARNIVAL}
		if (val2 != null) {
			return String.format("{%s : [%s, %s, %s]}", getName(), action.name(), val1, val2);
		} else {
			return String.format("{%s : [%s, %s]}", getName(), action.name(), val1);
		}
	}
	
	public void doAction(AutomationAgent agent) {
		switch (action) {
		case COMMAND:
			parent.getCommandSystem().invokeCommand(Command.valueOf(val1));
			break;
		case FIRE:
			parent.getEventForType(EventTypes.valueOf(val1)).fire();
			break;
		case ACTIVATE:
			parent.activateNextPlugin(val2, Main.SYSTEM_NAMES.valueOf(val1), agent.getDisplayName());
			break;
		}
	}
}
