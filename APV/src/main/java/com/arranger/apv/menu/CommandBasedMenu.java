package com.arranger.apv.menu;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;

public abstract class CommandBasedMenu extends BaseMenu {

	protected static class MenuCallback extends APVPlugin {
		
		@FunctionalInterface
		public interface MenuCommand {
			void onCommand();
		}

		private String text;
		private MenuCommand menuCommand;
		
		public MenuCallback(Main parent, String text, MenuCommand menuCommand) {
			super(parent);
			this.text = text;
			this.menuCommand = menuCommand;
		}

		@Override
		public void toggleEnabled() {
			menuCommand.onCommand();
		}

		@Override
		public String getDisplayName() {
			return text;
		}
	}
	
	public CommandBasedMenu(Main parent) {
		super(parent);
		showDetails = false;
	}

	protected void fireCommand(Command cmd) {
		parent.getCommandSystem().invokeCommand(cmd, getDisplayName(), 0);
	}

}
