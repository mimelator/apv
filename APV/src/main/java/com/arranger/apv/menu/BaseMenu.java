package com.arranger.apv.menu;

import java.util.ArrayList;
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
	protected boolean showDetails;
	protected boolean drawPlugin;
	protected boolean shouldSaveOnDeactivate = false;
	
	public BaseMenu(Main parent) {
		super(parent);
		menuPainter = new MenuPainter(parent, this);
		titlePainter = new TextPainter(parent);
		showDetails = true;
		drawPlugin = false;
		supportsExtendedConfig = false;
	}

	public void draw() {
		//Hack for: https://github.com/mimelator/apv/issues/15
		float transx = parent.width * .1f;
		parent.translate(-transx, 0);
		titlePainter.drawText(getTitleStrings(), LOCATION.UPPER_RIGHT);
		parent.translate(transx, 0);
		//Hack for: https://github.com/mimelator/apv/issues/15
		
		menuPainter.draw(showDetails);
		
		if (drawPlugin) {
			List<? extends APVPlugin> plugins = getPlugins();
			if (plugins != null && index < plugins.size()) {
				APVPlugin plugin = plugins.get(index);
				if (plugin instanceof ShapeSystem) {
					new SafePainter(parent, ()->{
						
						parent.scale(.5f);
						parent.translate(parent.width * .5f, parent.height * .5f);
						
						((ShapeSystem)plugin).draw();	
					}).paint();
				}
			}
		}
		
		List<String> msgs = new ArrayList<String>();
		if (shouldDrawResultMsg(msgs)) {
			new TextPainter(parent).drawText(msgs, LOCATION.UPPER_MIDDLE);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<String> getTitleStrings() {
		 return Arrays.asList(new String[] {getDisplayName()});
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
		return new PluginMenuItemAdapter(getPlugins().get(index), this.index == index);
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
		if (shouldSaveOnDeactivate) {
			parent.getConfigurator().saveCurrentConfig();
		}
		shouldSaveOnDeactivate = false;
	}
	
	protected boolean shouldDrawResultMsg(List<String> msgs) {
		return false;
	}
}
