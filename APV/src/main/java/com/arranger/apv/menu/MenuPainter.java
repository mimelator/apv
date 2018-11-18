package com.arranger.apv.menu;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

/**
 * This class only has one responsibility:
 *   Paint the menu items appropriately
 *   
 *  That mean:
 *  	use the correct font size and location
 *  	Draw whether the menu option is enabled/disabled
 *  	Draw whether the menu option is currently selected
 */
public class MenuPainter extends APVPlugin {

	public static final int INSET = 40;	//TODO make configurable and/or based off of screen size
	public static final int OFFSET = INSET;
	
	public static enum MENU_LOCATION {
		PRIMARY, SECONDARY
	}
	
	public static interface MenuItem {
		boolean isEnabled();
		boolean isSelected();
		String getText();
	}
	
	public static interface MenuProvider {
		int size();
		MenuItem getMenuItem(int index);
	}
	
	protected MenuProvider provider;
	protected MENU_LOCATION location;
	
	public MenuPainter(Main parent, MenuProvider provider, MENU_LOCATION location) {
		super(parent);
		this.location = location;
		this.provider = provider;
	}

	public void draw() {
		
		Point2D pt = getCoordinatesForLocation(location);
		int x = (int) pt.getX();
		int y = (int) pt.getY();
		parent.translate(x, y);
		int currentColor = parent.getColor().getCurrentColor().getRGB();
		
		int offset = 0;
		int count = provider.size();
		for (int index = 0; index < count; index++) {
			MenuItem item = provider.getMenuItem(index);
			
			if (item.isSelected()) {
				//TODO draw box around menu item
			}
			
			if (!item.isEnabled()) {
				//TODO fix styling Draw alternate
				parent.fill(Color.white.getRGB());
			} else {
				parent.fill(currentColor);
				parent.text(item.getText(), OFFSET, offset);
			}
			
			offset += OFFSET;
		}
		
		parent.translate(-x, -y);
	}
	
	public Point2D getCoordinatesForLocation(MENU_LOCATION location) {
		int x = 0, y = 0;
		
		switch (location) {
		case PRIMARY:
			x = OFFSET;
			y = INSET;			
			break;
		case SECONDARY:
			x = parent.width / 2;
			y = INSET;		
			break;
		}
		
		return new Point2D.Float(x, y);
	}

}
