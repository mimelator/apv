package com.arranger.apv.menu;

import java.awt.Color;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.helpers.Switch;
import com.arranger.apv.util.draw.TextPainter;

/**
 * This class only has one responsibility:
 *   Paint the menu items appropriately
 *   
 *  That means:
 *  	use the correct font size and location
 *  	Draw whether the menu option is enabled/disabled
 *  	Draw whether the menu option is currently selected
 */
public class MenuPainter extends APVPlugin {

	private static final int DISABLED_COLOR = Color.gray.getRGB();
	public static final int INSET = 40;	
	public static final int OFFSET = INSET;
	
	public static interface MenuItem {
		boolean isEnabled();
		boolean isSelected();
		String getText(boolean showDetails);
		APVPlugin getPlugin();
	}
	
	public static interface MenuProvider {
		int size();
		MenuItem getMenuItem(int index);
	}
	
	protected MenuProvider provider;
	protected TextPainter textPainter;
	
	public MenuPainter(Main parent, MenuProvider provider) {
		super(parent);
		this.provider = provider;
		textPainter = new TextPainter(parent);
	}
	
	public void draw(boolean showDetails) {
		//center stuff
		int x = OFFSET;
		int y = INSET;
		parent.translate(x, y);
		
		int currentColor = parent.getColor().getCurrentColor().getRGB();
		
		//calculate whether the count * (fontSize + spacing) is larger than
		// the height of the screen
		//  If so, reduce the text size
		int count = provider.size();
		float offset = OFFSET;
		float totalUsableHeight = parent.height - (2 * offset);
		
		if (offset * count > totalUsableHeight) {
			float heightPerRow = totalUsableHeight / count;
			float fontSize = heightPerRow * .80f;
			offset = heightPerRow;
			
			parent.textSize(fontSize);
		}
		
		parent.textAlign(LEFT, TOP);
		
		for (int index = 0; index < count; index++) {
			MenuItem item = provider.getMenuItem(index);
			if (item.isSelected()) {
				parent.rectMode(CORNER);
				parent.fill(currentColor);
				parent.rect(0, index * offset, offset / 2, offset);
				
				if (showDetails) {
					//draw the config
					String config = item.getPlugin().getConfig();
					parent.text(config, parent.width / 2, index * offset);
				}
			}

			//set the color
			int color = item.isEnabled() ? currentColor : DISABLED_COLOR;
			parent.fill(color);
			
			//can the item provide an alternate api
			boolean isSwitch = (item.getPlugin() instanceof Switch); 
			parent.text(item.getText(showDetails && !isSwitch) , offset, index * offset);
		}
		
		parent.translate(-x, -y);
	}
}
