package com.arranger.apv.menu;

import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.menu.MenuPainter.MenuItem;
import com.arranger.apv.menu.MenuPainter.MenuProvider;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.SafePainter.LOCATION;
import com.arranger.apv.util.draw.TextPainter;

import edu.emory.mathcs.backport.java.util.Arrays;


public abstract class BaseMenu extends APVPlugin implements MenuProvider {
	
	protected int index;
	protected MenuPainter menuPainter;
	protected TextPainter titlePainter;
	protected List<String> title;
	protected boolean showDetails;
	protected boolean drawPlugin;
	
	@SuppressWarnings("unchecked")
	public BaseMenu(Main parent) {
		super(parent);
		menuPainter = new MenuPainter(parent, this);
		titlePainter = new TextPainter(parent);
		title = Arrays.asList(new String[] {getDisplayName()});
		showDetails = true;
		drawPlugin = false;
	}
	
	public void draw() {
		titlePainter.drawText(title, LOCATION.UPPER_RIGHT);
		menuPainter.draw(showDetails);
		
		if (drawPlugin) {
			APVPlugin plugin = getPlugins().get(index);
			if (plugin instanceof ShapeSystem) {
				new SafePainter(parent, ()->{
					
					parent.scale(.5f);
					parent.translate(parent.width * .5f, parent.height * .5f);
					
					((ShapeSystem)plugin).draw();	
				}).paint();
			}
		}
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
	
	public BaseMenu getChildMenu() {
		return getChildMenu(index);
	}
	
	public BaseMenu getChildMenu(int index) {
		return (BaseMenu)getPlugins().get(index);
	}
	
	public void onActivate() {
		
	}
	
	public void onDeactivate() {
		
	}
}
