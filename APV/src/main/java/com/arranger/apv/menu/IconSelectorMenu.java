package com.arranger.apv.menu;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.filechooser.FileNameExtensionFilter;

import com.arranger.apv.Main;
import com.arranger.apv.menu.ImageSelectorMenu.ImageSelector;
import com.arranger.apv.util.ImageHelper;
import com.arranger.apv.util.ImageHelper.ICON_NAMES;
import com.arranger.apv.util.draw.SafePainter.LOCATION;
import com.arranger.apv.util.draw.TextPainter;

import processing.core.PImage;

public class IconSelectorMenu extends ImageSelector {
	
	private static final String DIRECTIONS = "Choose a group of images to use as backgrounds.\n" + 
			"  The images must be PNGs and look much better with transparent sections..  \n" + 
			"  If you use images larger than 1MB the program might crash or run erratically. \n" + 
			"  After choosing the images, you can navigate to the Plugins Menu->Foregrounds to see some of the Sprite Factory foregrounds which use icons.. ";
	
	private List<Path> paths;

	public IconSelectorMenu(Main parent) {
		super(parent);
	}

	@Override
	public void draw() {
		super.draw();
		if (paths != null) {
			List<String> loadedImages = paths.stream().map(p -> p.toString()).collect(Collectors.toList());
			loadedImages.add(0, "Added Icons");
			new TextPainter(parent).drawText(loadedImages, LOCATION.UPPER_MIDDLE);
		}
	}
	
	@Override
	public void onActivate() {
		super.onActivate();
		paths = null;
	}
	
	@Override
	protected String getMenuTitle() {
		return "Load Emojis";
	}
	
	@Override
	protected String getDirections() {
		return DIRECTIONS;
	}
	
	@Override
	protected FileNameExtensionFilter getFileFilter() {
		return new FileNameExtensionFilter("PNGs", "png");
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
