package com.arranger.apv.menu;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.systems.lite.FreqDetector;

public class AudioTestMenu extends CommandBasedMenu {

	protected static class MenuAdapterCallback extends APVPlugin {
		
		@FunctionalInterface
		public interface MenuCommand {
			void onCommand();
		}

		private String text;
		private MenuCommand menuCommand;
		
		public MenuAdapterCallback(Main parent, String text, MenuCommand menuCommand) {
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
	
	protected FreqDetector freqDetector;
	
	
	public AudioTestMenu(Main parent) {
		super(parent);
		freqDetector = new FreqDetector(parent);
		showDetails = false;
	}
	
	public void draw() {
		super.draw();
		freqDetector.draw();
	}

	@Override
	public List<? extends APVPlugin> getPlugins() {
		List<MenuAdapterCallback> results = new ArrayList<MenuAdapterCallback>();
		
		results.add(new MenuAdapterCallback(parent, Command.AUDIO_INC.getDisplayName(), ()-> fireCommand(Command.AUDIO_INC)));
		results.add(new MenuAdapterCallback(parent, Command.AUDIO_DEC.getDisplayName(), ()-> fireCommand(Command.AUDIO_DEC)));
		
		return results;
	}
}
