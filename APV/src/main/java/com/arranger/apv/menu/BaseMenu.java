package com.arranger.apv.menu;

import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.menu.MenuPainter.MENU_LOCATION;
import com.arranger.apv.menu.MenuPainter.MenuItem;
import com.arranger.apv.menu.MenuPainter.MenuProvider;


public abstract class BaseMenu extends APVPlugin implements MenuProvider {
	
	protected int index;
	protected MenuPainter menuPainter;
	
	public BaseMenu(Main parent, boolean isPrimary) {
		super(parent);
		menuPainter = new MenuPainter(parent, this, isPrimary ? MENU_LOCATION.PRIMARY : MENU_LOCATION.SECONDARY);
	}
	
	public void draw() {
		menuPainter.draw();
		
		//TODO draw instructions
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
}
