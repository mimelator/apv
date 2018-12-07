package com.arranger.apv.menu.img;

import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import com.arranger.apv.Main;
import com.arranger.apv.util.ImageHelper;
import com.arranger.apv.util.ImageHelper.ICON_NAMES;

import processing.core.PImage;

public class IconSelector extends ImageSelector {
	
	private static final String DIRECTIONS = "Choose a group of images to use as backgrounds.\n" + 
			"  The images must be PNGs and look much better with transparent sections..  \n" + 
			"  If you use images larger than 1MB the program might crash or run erratically. \n" + 
			"  After choosing the images, you can navigate to the Plugins Menu->Foregrounds to see some of the Sprite Factory foregrounds which use icons.. ";
	
	private List<Path> paths;

	public IconSelector(Main parent) {
		super(parent);
	}

	@Override
	public void onActivate() {
		super.onActivate();
		paths = null;
	}
	
	@Override
	protected boolean shouldDrawResultMsg(List<String> msgs) {
		if (paths != null) {
			msgs.addAll(paths.stream().map(p -> p.toString()).collect(Collectors.toList()));
			msgs.add(0, "Added Icons");
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected String getMenuTitle() {
		return "Load Icons";
	}
	
	@Override
	protected String getDirections() {
		return DIRECTIONS;
	}
	
	@Override
	protected FilenameFilter getFileFilter() {
		return ((dir, name) -> name.endsWith("png")); 
	}

	@Override
	protected void onSelectedImages(List<Path> paths) {
		this.paths = paths;
		ImageHelper imageHelper = parent.getImageHelper();
		for (Path p : paths) {
			String pathString = p.toString();
			PImage image = parent.loadImage(pathString);
			if (image != null) {
				imageHelper.updateImage(ICON_NAMES.random(), image, pathString);
			}
		}
	}
}
