package com.arranger.apv.menu;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

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
	
	/**
	 * calculate whether the count * (fontSize + spacing) is larger than
		 the height of the screen
		If so, reduce the text size
	 */
	protected boolean isOverflowMenu() {
		float totalUsableHeight = parent.height - (2 * OFFSET);
		return (OFFSET * provider.size() > totalUsableHeight);
	}
	
	protected float getHeightPerRow() {
		float totalUsableHeight = parent.height - (2 * OFFSET);
		int count = provider.size();
		return totalUsableHeight / count;
	}
	
	protected float getCalculatedOffsetPerRow() {
		if (isOverflowMenu()) {
			return getHeightPerRow();
		} else {
			return OFFSET;
		}
	}
	
	public void draw(boolean showDetails) {
		//center stuff
		int x = OFFSET;
		int y = INSET;
		parent.translate(x, y);
		
		Color currentColorObj = parent.getColor().getCurrentColor();
		int currentColor = currentColorObj.getRGB();
		
		float offset = getCalculatedOffsetPerRow();
		float vOffset = (.1f * offset);
		
		//TODO What do for mouse selection?
		if (isOverflowMenu()) {
			float fontSize = getCalculatedOffsetPerRow() * .80f;
			parent.textSize(fontSize);
		}
		
		parent.textAlign(LEFT, TOP);
		
		for (int index = 0; index < provider.size(); index++) {
			MenuItem item = provider.getMenuItem(index);
			if (item.isSelected()) {
				
				//@SEE #getRectForMenuItem
				float top_left_y = (index * offset) + vOffset;
				
				//lighter full row
				parent.rectMode(CORNER);
				parent.fill(currentColorObj.brighter().brighter().getRGB(), 127);
				parent.rect(0, top_left_y, parent.width * .8f, offset); 
				
				//Little cursor
				parent.fill(currentColor);
				parent.rect(0, top_left_y, offset / 2, offset);
				
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
	
	public Rectangle2D getRectForMenuItem(int index, boolean includeScreenOffsets) {
		float offset = getCalculatedOffsetPerRow();
		float vOffset = (.1f * offset);
		float top_left_y = (index * offset) + vOffset;
		
		if (includeScreenOffsets) {
			return new Rectangle2D.Float(0 + offset, top_left_y + offset, parent.width * .8f, offset);
		} else {
			return new Rectangle2D.Float(0, top_left_y, parent.width * .8f, offset);
		}
	}
}
