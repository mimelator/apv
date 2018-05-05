package com.arranger.apv.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import processing.core.PImage;

public class ImageHelper extends APVPlugin {
	
	@FunctionalInterface
	public static interface ImageChangeHandler {
		void onImageChange(PImage image);
	}
	
	private Map<String, List<ImageChangeHandler>> handlers = new HashMap<String, List<ImageChangeHandler>>();
	
	public ImageHelper(Main parent) {
		super(parent);
	}

	public PImage loadImage(String path, ImageChangeHandler handler) {
		List<ImageChangeHandler> list = handlers.get(path);
		if (list == null) {
			list = new ArrayList<ImageChangeHandler>();
			handlers.put(path, list);
		}
		list.add(handler);
		return parent.loadImage(path);
	}

	public void updateImage(String path, PImage image) {
		List<ImageChangeHandler> list = handlers.get(path);
		if (list != null) {
			list.forEach(h -> h.onImageChange(image));
		}
	}
}

