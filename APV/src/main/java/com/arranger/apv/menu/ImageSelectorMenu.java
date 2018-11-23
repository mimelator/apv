package com.arranger.apv.menu;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.shader.Shader.SHADERS;
import com.arranger.apv.util.DynamicShaderHelper;
import com.arranger.apv.util.FileHelper;

public class ImageSelectorMenu extends CommandBasedMenu {
	
	private float DEFAULT_ALPHA = .5f;
	
	private FileHelper fileHelper;

	public ImageSelectorMenu(Main parent) {
		super(parent);
		fileHelper = new FileHelper(parent);
	}

	@Override
	public List<? extends APVPlugin> getPlugins() {
		List<MenuCallback> results = new ArrayList<MenuCallback>();
		results.add(new MenuCallback(parent, "Load Background Images", ()-> onLoadImagesSelection()));
		return results;
	}

	protected void onLoadImagesSelection() {
		JFileChooser fc = fileHelper.getJFileChooser();
		fc.setMultiSelectionEnabled(true);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileNameExtensionFilter("JPGs", "jpg"));
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			List<Path> paths = new ArrayList<Path>();
			Arrays.asList(fc.getSelectedFiles()).forEach(file -> paths.add(file.toPath()));
			new DynamicShaderHelper(parent).loadBackgrounds(parent, DEFAULT_ALPHA, SHADERS.VALUES, paths, true);
		}
	}
}
