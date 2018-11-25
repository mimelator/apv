package com.arranger.apv.menu;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public abstract class CommandBasedMenu extends BaseMenu {

	public CommandBasedMenu(Main parent) {
		super(parent);
		showDetails = false;
	}

	protected void fireCommand(Command cmd) {
		parent.getCommandSystem().invokeCommand(cmd, getDisplayName(), 0);
	}

}
