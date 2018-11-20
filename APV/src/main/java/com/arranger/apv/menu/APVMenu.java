package com.arranger.apv.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.util.KeyListener.KEY_SYSTEMS;
import com.arranger.apv.util.KeyListener.KeyEventListener;

import processing.event.KeyEvent;

/**
 * 
	Master menu:
	  * Switches
	  * Plugins (Full Systems)
	  * Commands (Basic/Advanced)
	  * Configuration
	     Save, Reload, Load?
	 
	 Each Menu has
	  	An Active outline
	  	Up / Down keyboard strokes for navigation
	  	A Back 'button'
	 
	 There needs to be instructions at the bottom which talk about using the arrow keys and the space bar or Esc to exit
	 the current menu or the menu mode
 *
 */
public class APVMenu extends APV<BaseMenu> implements KeyEventListener {

	@FunctionalInterface
	interface MenuCommand {
		void onCommand();
	}

	private Map<String, MenuCommand> keyBindingMap = new HashMap<String, MenuCommand>();
	private MainMenu mainMenu;
	private BaseMenu currentMenu; 
	
	public APVMenu(Main parent) {
		super(parent, Main.SYSTEM_NAMES.MENU, false);
		mainMenu = new MainMenu(parent);
		
		sw.observable.addObserver((o, a) -> {
			if (sw.isEnabled()) {
				parent.getKeyListener().setSystem(KEY_SYSTEMS.MENU);
				currentMenu = mainMenu;
				currentMenu.setIndex(0);
			} else {
				parent.getKeyListener().setSystem(KEY_SYSTEMS.COMMAND);
			}
		});
		
		keyBindingMap.put("m", () -> onExit());
		keyBindingMap.put("↑", () -> onUp());
		keyBindingMap.put("↓", () -> onDown());
		keyBindingMap.put(" ", () -> onSelect());
		keyBindingMap.put("\n", () -> onSelect());
		keyBindingMap.put("⌫", () -> onBack());
	}

	protected void onExit() {
		parent.getCommandSystem().invokeCommand(Command.SWITCH_MENU, getDisplayName(), 0);
	}
	
	protected void onUp() {
		int i = currentMenu.getIndex();
		if (i > 0) {
			currentMenu.setIndex(i - 1);
		}
	}
	
	protected void onDown() {
		int i = currentMenu.getIndex();
		if (i < currentMenu.size() - 1) {
			currentMenu.setIndex(i + 1);
		}
	}

	protected void onBack() {
		System.out.println("onBack");
		if (currentMenu != mainMenu) {
			currentMenu = mainMenu;
		} else {
			onExit();
		}
	}
	
	protected void onSelect() {
		if (currentMenu == mainMenu) {
			//activate the appropriate menu
			currentMenu = (BaseMenu)mainMenu.getPlugins().get(mainMenu.getIndex());
		} else {
			//for the main menu, this should drill into other menus
			currentMenu.getPlugins().get(currentMenu.getIndex()).toggleEnabled();
		}
		
		//TODO: update the disabled plugin list
	}
	
	public void drawMenu() {
		parent.fill(0);
		parent.rect(0, 0, parent.width, parent.height);
		
		currentMenu.draw();
	}
	
	public void onKeyEvent(KeyEvent keyEvent) {
		String keyForKeyEvent = Command.getKeyForKeyEvent(keyEvent);
		if (keyBindingMap.containsKey(keyForKeyEvent)) {
			keyBindingMap.get(keyForKeyEvent).onCommand();
		}
	}
	
	public class MainMenu extends BaseMenu {
		
		public MainMenu(Main parent) {
			super(parent, true);
		}

		@Override
		public List<? extends APVPlugin> getPlugins() {
			return getList();
		}
	}
}
