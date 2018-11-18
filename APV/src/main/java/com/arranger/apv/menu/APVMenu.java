package com.arranger.apv.menu;

import com.arranger.apv.APV;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.menu.MenuPainter.MENU_LOCATION;
import com.arranger.apv.menu.MenuPainter.MenuItem;
import com.arranger.apv.menu.MenuPainter.MenuProvider;
import com.arranger.apv.util.KeyListener.KEY_SYSTEMS;
import com.arranger.apv.util.KeyListener.KeyEventListener;

import processing.event.KeyEvent;

/**
 * 
 * 		//Configuration
		// Not sure what to do with the reference.conf plugins
		// Do i make things configurable?
		// If so, then i probably need to ensure that when starting the menu system
		// it goes to the main menu first.  However, that's only at the 'start' of a menu 'session'
		
		//key strokes
		// At the start (and end) of a menu 'session', i'll need to capture the keyboard input
		// Like the Command Manager: parent.registerMethod("keyEvent", this);
		// I'll need to disable the CM when the MenuM is active
		// I could create a custom APV class for the Menu
		// It will handle all the menu plugins as well as the 'session' mgmt required for keystrokes
		
		
		// Master menu:
		//  * Switches
		//  * Plugins (Full Systems)
		//  * Commands (Basic/Advanced)
		//  * Configuration
		//     Save, Reload, Load?
		// Each Menu has
		//  An Active outline
		//  Up / Down arrows for navigation
		//  A Back 'button'
		
		// Probably have various menus encapsulate ListHelper
		
		
		//List Helper
		//How do i capture keyboard input and then pass to the currently active list?
 *
 */
public class APVMenu extends APV<StandardMenu> implements KeyEventListener, MenuProvider {

	private MenuPainter menuPainter;
	
	public APVMenu(Main parent) {
		super(parent, Main.SYSTEM_NAMES.MENU, false);
		
		sw.observable.addObserver((o, a) -> {
			if (sw.isEnabled()) {
				prepareMenu();
			} else {
				closeMenu();
			}
		});
		
		menuPainter = new MenuPainter(parent, this, MENU_LOCATION.PRIMARY);
	}

	public void drawMenu() {
		parent.fill(0);
		parent.rect(0, 0, parent.width, parent.height);
		
		menuPainter.draw();
	}
	
	public void onKeyEvent(KeyEvent keyEvent) {
		System.out.println("Getting key Event");
		char charKey = keyEvent.getKey();
		if (charKey == 'm') {
			parent.getCommandSystem().invokeCommand(Command.SWITCH_MENU, getDisplayName(), 0);
		}
	}
	
	@Override
	public int size() {
		return list.size();
	}

	@Override
	public MenuItem getMenuItem(int index) {
		return new MenuItemAdapter(list.get(index));
	}

	protected void prepareMenu() {
		System.out.println("prepareMenu");
		parent.getKeyListener().setSystem(KEY_SYSTEMS.MENU);
		
	}
	
	protected void closeMenu() {
		System.out.println("closeMenu");
		parent.getKeyListener().setSystem(KEY_SYSTEMS.COMMAND);
	}
}
