package com.arranger.apv.menu;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.draw.SafePainter;

public class ImageSelectorMenu extends CommandBasedMenu {
	
	public abstract static class ImageSelector extends BaseMenu {
		
		public ImageSelector(Main parent) {
			super(parent);
		}
		
		@Override
		public List<? extends APVPlugin> getPlugins() {
			return Arrays.asList(new APVPlugin[0]);
		}
		
		protected abstract void onSelectedImages(List<Path> paths);
		protected abstract String getMenuTitle();
		protected abstract String getDirections();
		protected abstract FileNameExtensionFilter getFileFilter();
	}
	
	public static class ImageAdapterCallback extends APVPlugin {
		
		@FunctionalInterface
		public interface MenuCommand {
			void onCommand();
		}

		private ImageSelector imageSelector;
		private MenuCommand menuCommand;
		
		
		public ImageAdapterCallback(Main parent, ImageSelector imageSelector, MenuCommand menuCommand) {
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
	
	private FileHelper fileHelper;
	private JFileChooser fc;
	private List<ImageAdapterCallback> menuItems = new ArrayList<ImageAdapterCallback>();

	public ImageSelectorMenu(Main parent) {
		super(parent);
		
		fileHelper = new FileHelper(parent);
		
		menuItems.add(new ImageAdapterCallback(parent, new BackgroundSelectorMenu(parent), ()-> onLoadImagesSelection()));
		menuItems.add(new ImageAdapterCallback(parent, new IconSelectorMenu(parent), ()-> onLoadImagesSelection()));
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
		fc = fileHelper.getJFileChooser();
		menuItems.forEach(mac -> mac.getImageSelector().onActivate());
	}
	
	@Override
	public void onDeactivate() {
		super.onDeactivate();
		menuItems.forEach(mac -> mac.getImageSelector().onDeactivate());
		fc = null;
	}
	
	@Override
	public List<? extends APVPlugin> getPlugins() {
		return menuItems;
	}
	
	protected ImageSelector getCurrentSelector() {
		return menuItems.get(getIndex()).getImageSelector();
	}

	protected void onLoadImagesSelection() {
		ImageSelector imageSelector = getCurrentSelector();
		
		//There's some double selection going on.  For right now, this is a bit of a hack
		//If the user dismisses the dialog with a keyboard stroke that gets routed back here which we don't want
		if (fc != null) {
			fc.setMultiSelectionEnabled(true);
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(imageSelector.getFileFilter());
			if (fc.showOpenDialog(parent.frame) == JFileChooser.APPROVE_OPTION) {
				List<Path> paths = new ArrayList<Path>();
				Arrays.asList(fc.getSelectedFiles()).forEach(file -> paths.add(file.toPath()));
				imageSelector.onSelectedImages(paths);
				fc = null; //hack
			}
		}
	}
}
