package com.arranger.apv.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.Main.SYSTEM_NAMES;
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
	
	public static final String DIRECTIONS = "Use the Arrow keys to navigate up and down the list.\n" +
			"  Use the left and right Arrow keys to vote up or down the 'popularity' of the plugins.\n" +
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
				currentMenu.onActivate();
			} else {
				parent.getKeyListener().setSystem(KEY_SYSTEMS.COMMAND);
			}
		});
		
		keyBindingMap.put("m", () -> onExit());
		keyBindingMap.put("↑", () -> onUp());
		keyBindingMap.put("↓", () -> onDown());
		keyBindingMap.put("←", () -> onIncrement(-1));
		keyBindingMap.put("→", () -> onIncrement(1));
		keyBindingMap.put(" ", () -> onSelect());
		keyBindingMap.put("\n", () -> onSelect());
		keyBindingMap.put("⌫", () -> onBack());
	}

	protected void onExit() {
		currentMenu.onDeactivate();
		parent.getCommandSystem().invokeCommand(Command.SWITCH_MENU, getDisplayName(), 0);
	}
	
	protected void onIncrement(int i) {
		APVPlugin cp = getCurrentPlugin();
		cp.setPopularityIndex(cp.getPopularityIndex() + i);
		currentMenu.shouldSaveOnDeactivate = true;
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
			currentMenu.onDeactivate();
			currentMenu = menuStack.pop();
			currentMenu.onActivate();
		} else {
			onExit();
		}
	}
	
	protected void onSelect() {
		if (currentMenu.hasChildMenus()) {
			menuStack.push(currentMenu);
			currentMenu = currentMenu.getChildMenu();
			currentMenu.onActivate();
		} else {
			//select it
			getCurrentPlugin().toggleEnabled();
			currentMenu.shouldSaveOnDeactivate = true;
		}
	}

	protected APVPlugin getCurrentPlugin() {
		return currentMenu.getPlugins().get(currentMenu.getIndex());
	}
	
	public void drawMenu() {
		//prep and draw menu
		parent.fill(0);
		parent.rect(0, 0, parent.width, parent.height);
		currentMenu.draw();

		//Directions
		new SafePainter(parent, ()-> {
			parent.textAlign(CENTER);
			parent.textSize(parent.getGraphics().textSize * .75f);
			parent.text(DIRECTIONS, parent.width / 2, parent.height * .85f);
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
	
	List<? extends APVPlugin> getAPVPluginList() {
		List<APV<? extends APVPlugin>> results = new ArrayList<APV<? extends APVPlugin>>();
		results.add(parent.getAgent());
		results.add(parent.getBackgrounds());
		results.add(parent.getBackDrops());
		results.add(parent.getSystem(SYSTEM_NAMES.CONTROLS));
		results.add(parent.getFilters());
		results.add(parent.getForegrounds());
		results.add(parent.getLocations());
		results.add(parent.getShaders());
		results.add(parent.getTransitions());
		results.add(parent.getWatermark());
		return results;
	}
}
