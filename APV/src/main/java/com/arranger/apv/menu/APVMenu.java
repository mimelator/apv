package com.arranger.apv.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.util.KeyListener.KEY_SYSTEMS;
import com.arranger.apv.util.KeyListener.KeyEventListener;
import com.arranger.apv.util.draw.SafePainter;

import processing.event.KeyEvent;

/**
 * Master menu:
	  * Switches
	  * Plugins (Full Systems)
	  * Commands (Basic/Advanced)
	  * Backgrounds, Emojis, icons
	  * Configuration
	     Save, Reload, Load?
 */
public class APVMenu extends APV<BaseMenu> implements KeyEventListener {
	
	public static final String DIRECTIONS = "Use the Arrow keys to navigate up and down the list\n" + 
			"  Press the spacebar or the enter key to select a menu item.  \n" + 
			"  This will either drill into the next menu or enable / disable the current item.\n" + 
			"  Press the Backspace (delete) to return to the previous menu or to exit the main menu";

	@FunctionalInterface
	interface MenuCommand {
		void onCommand();
	}

	private Map<String, MenuCommand> keyBindingMap = new HashMap<String, MenuCommand>();
	private MainMenu mainMenu;
	private BaseMenu currentMenu; 
	private Stack<BaseMenu> menuStack;
	
	public APVMenu(Main parent) {
		super(parent, Main.SYSTEM_NAMES.MENU, false);
		mainMenu = new MainMenu(parent);
		menuStack = new Stack<BaseMenu>();
		
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
		if (currentMenu != mainMenu) {
			currentMenu = menuStack.pop();
		} else {
			onExit();
		}
	}
	
	protected void onSelect() {
		if (currentMenu.hasChildMenus()) {
			menuStack.push(currentMenu);
			currentMenu = currentMenu.getChildMenu();
		} else {
			//select it
			currentMenu.getPlugins().get(currentMenu.getIndex()).toggleEnabled();
			
			//TODO: update the disabled plugin list for serialization
		}
	}
	
	public void drawMenu() {
		parent.fill(0);
		parent.rect(0, 0, parent.width, parent.height);
		
		currentMenu.draw();
		
		new SafePainter(parent, ()-> {
			parent.textAlign(CENTER);
			parent.text(DIRECTIONS, parent.width / 2, parent.height * .8f);
		}).paint();
	}
	
	public void onKeyEvent(KeyEvent keyEvent) {
		String keyForKeyEvent = Command.getKeyForKeyEvent(keyEvent);
		if (keyBindingMap.containsKey(keyForKeyEvent)) {
			keyBindingMap.get(keyForKeyEvent).onCommand();
		}
	}
	
	public class MainMenu extends BaseMenu {
		
		public MainMenu(Main parent) {
			super(parent);
			showDetails = false;
		}

		@Override
		public List<? extends APVPlugin> getPlugins() {
			return getList();
		}

		@Override
		public boolean hasChildMenus() {
			return true;
		}
	}
}
