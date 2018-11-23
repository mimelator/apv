package com.arranger.apv.menu;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.shader.Shader.SHADERS;
import com.arranger.apv.shader.Watermark;
import com.arranger.apv.util.DynamicShaderHelper;
import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.draw.SafePainter;
import com.arranger.apv.util.draw.SafePainter.LOCATION;
import com.arranger.apv.util.draw.TextPainter;

public class ImageSelectorMenu extends CommandBasedMenu {
	
	private static final float DEFAULT_ALPHA = .5f;
	public static final String DIRECTIONS = "Choose a group of images to use as backgrounds.\n" + 
			"  The images must be JPG images and end with the .jpg extension.  \n" + 
			"  If you use images larger than 1MB the program might crash or run erratically. \n" + 
			"  After choosing the photos, you can navigate to the Plugins Menu->Shaders to see that they were registered. ";
	
	private FileHelper fileHelper;
	private JFileChooser fc;
	private List<Watermark> watermarks;
	private List<MenuCallback> menuItems = new ArrayList<MenuCallback>();
	
	public ImageSelectorMenu(Main parent) {
		super(parent);
		fileHelper = new FileHelper(parent);
		menuItems.add(new MenuCallback(parent, "Load Background Images", ()-> onLoadImagesSelection()));
	}
	
	@Override
	public void draw() {
		super.draw();
		new SafePainter(parent, ()-> {
			parent.textAlign(CENTER);
			parent.textSize(parent.getGraphics().textSize * .75f);
			parent.text(DIRECTIONS, parent.width / 2, parent.height * .5f);			
		}).paint();
		
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
		fc = fileHelper.getJFileChooser();
	}

	@Override
	public List<? extends APVPlugin> getPlugins() {
		return menuItems;
	}

	protected void onLoadImagesSelection() {
		//There's some double selection going on.  For right now, this is a bit of a hack
		if (fc != null) {
			fc.setMultiSelectionEnabled(true);
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new FileNameExtensionFilter("JPGs", "jpg"));
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				List<Path> paths = new ArrayList<Path>();
				Arrays.asList(fc.getSelectedFiles()).forEach(file -> paths.add(file.toPath()));
				watermarks = new DynamicShaderHelper(parent).loadBackgrounds(parent, DEFAULT_ALPHA, SHADERS.VALUES, paths, true);
				fc = null; //hack
			}
		}
	}
}
