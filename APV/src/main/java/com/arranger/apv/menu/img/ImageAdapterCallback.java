package com.arranger.apv.menu.img;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class ImageAdapterCallback extends APVPlugin {
	
	@FunctionalInterface
	public interface MenuCommand {
		void onCommand();
	}

	private ImageSelector imageSelector;
	private ImageAdapterCallback.MenuCommand menuCommand;
	
	
	public ImageAdapterCallback(Main parent, ImageSelector imageSelector, ImageAdapterCallback.MenuCommand menuCommand) {
		super(parent);
		this.imageSelector = imageSelector;
		this.menuCommand = menuCommand;
	}

	@Override
	public void toggleEnabled() {
		menuCommand.onCommand();
	}

	@Override
	public String getDisplayName() {
		return imageSelector.getDisplayName();
	}
	
	public ImageSelector getImageSelector() {
		return imageSelector;
	}
}