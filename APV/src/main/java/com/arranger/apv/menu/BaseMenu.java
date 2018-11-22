package com.arranger.apv.menu;

import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.menu.MenuPainter.MENU_LOCATION;
import com.arranger.apv.menu.MenuPainter.MenuItem;
import com.arranger.apv.menu.MenuPainter.MenuProvider;
import com.arranger.apv.util.draw.SafePainter.LOCATION;

import edu.emory.mathcs.backport.java.util.Arrays;

import com.arranger.apv.util.draw.TextPainter;


public abstract class BaseMenu extends APVPlugin implements MenuProvider {
	
	protected int index;
	protected MenuPainter menuPainter;
	protected TextPainter titlePainter;
	protected List<String> title;
	
	@SuppressWarnings("unchecked")
	public BaseMenu(Main parent, boolean isPrimary) {
		super(parent);
		menuPainter = new MenuPainter(parent, this, isPrimary ? MENU_LOCATION.PRIMARY : MENU_LOCATION.SECONDARY);
		titlePainter = new TextPainter(parent);
		title = Arrays.asList(new String[] {getDisplayName()});
	}
	
	public void draw() {
		//TODO draw menu name
		titlePainter.drawText(title, LOCATION.UPPER_RIGHT);
		
		
		menuPainter.draw();
		
		//TODO draw instructions
	}
	
	/**
	 * If you have child menus, you better override getChildMenu
	 */
	public boolean hasChildMenus() {
		return false;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public abstract List<? extends APVPlugin> getPlugins();
	
	@Override
	public int size() {
		return getPlugins().size();
	}

	@Override
	public MenuItem getMenuItem(int index) {
		return new MenuItemAdapter(getPlugins().get(index), this.index == index);
	}
	
	/**
	 * Get the menu at the current index
	 */
	public BaseMenu getChildMenu() {
		return getChildMenu(index);
	}
	
	public BaseMenu getChildMenu(int index) {
		return (BaseMenu)getPlugins().get(index);
	}
}
