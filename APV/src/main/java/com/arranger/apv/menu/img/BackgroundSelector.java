package com.arranger.apv.menu.img;

import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import com.arranger.apv.Main;
import com.arranger.apv.shader.Shader.SHADERS;
import com.arranger.apv.shader.Watermark;
import com.arranger.apv.util.DynamicShaderHelper;
import com.arranger.apv.util.draw.SafePainter.LOCATION;
import com.arranger.apv.util.draw.TextPainter;

public class BackgroundSelector extends ImageSelector {
	
	private static final float DEFAULT_ALPHA = .5f;
	private static final String DIRECTIONS = "Choose a group of images to use as backgrounds.\n" + 
			"  The images must be JPG images and end with the .jpg extension.  \n" + 
			"  If you use images larger than 1MB the program might crash or run erratically. \n" + 
			"  After choosing the photos, you can navigate to the Plugins Menu->Shaders to see that they were registered. ";
	
	private List<Watermark> watermarks;

	public BackgroundSelector(Main parent) {
		super(parent);
	}

	@Override
	public void draw() {
		super.draw();
		if (watermarks != null) {
			List<String> loadedImages = watermarks.stream().map(wm -> wm.getDisplayName()).collect(Collectors.toList());
			loadedImages.add(0, "Added Images");
			new TextPainter(parent).drawText(loadedImages, LOCATION.UPPER_MIDDLE);
		}
	}
	
	@Override
	public void onActivate() {
		super.onActivate();
		watermarks = null;
	}
	
	@Override
	protected String getMenuTitle() {
		return "Load Background Images";
	}
	
	@Override
	protected String getDirections() {
		return DIRECTIONS;
	}
	
	@Override
	protected FilenameFilter getFileFilter() {
		return ((dir, name) -> name.endsWith("jpg")); 
	}

	@Override
	protected void onSelectedImages(List<Path> paths) {
		watermarks = new DynamicShaderHelper(parent).loadBackgrounds(parent, DEFAULT_ALPHA, SHADERS.VALUES, paths, true);
	}
}
