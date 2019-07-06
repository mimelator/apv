package com.arranger.apv.menu.img;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.menu.CommandBasedMenu;
import com.arranger.apv.util.draw.SafePainter;

public class ImageSelectorMenu extends CommandBasedMenu {
	
	private List<ImageAdapterCallback> menuItems = new ArrayList<ImageAdapterCallback>();

	public ImageSelectorMenu(Main parent) {
		super(parent);
		
		menuItems.add(new ImageAdapterCallback(parent, new BackgroundSelector(parent), ()-> onLoadImagesSelection()));
		menuItems.add(new ImageAdapterCallback(parent, new IconSelector(parent), ()-> onLoadImagesSelection()));
	}
	
	@Override
	public void draw() {
		super.draw();
		new SafePainter(parent, ()-> {
			parent.textAlign(CENTER);
			parent.textSize(parent.getGraphics().textSize * .75f);
			parent.text(getCurrentSelector().getDirections(), parent.width / 2, parent.height * .5f);			
		}).paint();
		
		getCurrentSelector().draw();
	}

	@Override
	public void onActivate() {
		super.onActivate();
		menuItems.forEach(mac -> mac.getImageSelector().onActivate());
	}
	
	@Override
	public void onDeactivate() {
		super.onDeactivate();
		menuItems.forEach(mac -> mac.getImageSelector().onDeactivate());
	}
	
	@Override
	public List<? extends APVPlugin> getPlugins() {
		return menuItems;
	}
	
	@Override
	protected List<String> getTitleStrings() {
		return new ArrayList<String>();
	}
	
	protected ImageSelector getCurrentSelector() {
		return menuItems.get(getIndex()).getImageSelector();
	}

	protected void onLoadImagesSelection() {
		parent.selectFolder("Select folder", file -> {
			ImageSelector currentSelector = getCurrentSelector();
			List<Path> paths = new ArrayList<Path>();
			Arrays.asList(file.listFiles(currentSelector.getFileFilter())).forEach(f -> paths.add(f.toPath()));
			currentSelector.onSelectedImages(paths);
		});
	}
}
