package com.arranger.apv.menu.img;

import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.menu.BaseMenu;

public abstract class ImageSelector extends BaseMenu {
	
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
	protected abstract FilenameFilter getFileFilter();
}