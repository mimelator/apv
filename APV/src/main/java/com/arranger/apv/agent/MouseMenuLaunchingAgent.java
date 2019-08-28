package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.util.KeyListener;

public class MouseMenuLaunchingAgent extends BaseAgent {

	public MouseMenuLaunchingAgent(Main parent) {
		super(parent);
		
		parent.getMousePulseEvent().register(() -> {
			onDraw();
		});
	}

	protected void onDraw() {
		//is menu current active? if so then ignore
		boolean isMenuActive = parent.getKeyListener().getSystem() == KeyListener.KEY_SYSTEMS.MENU;
		if (isMenuActive) {
			return;
		} else {
			//if mouse is clicked, open the menu (SWITCH_MENU)
			invokeCommand(Command.SWITCH_MENU);
		}
	}
}
