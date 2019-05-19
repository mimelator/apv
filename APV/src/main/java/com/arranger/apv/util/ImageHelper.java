package com.arranger.apv.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.Texture;

public class ImageHelper extends APVPlugin {
	
	private static final String CONFIG_HEADER = "#image list" + System.lineSeparator();
	
	@FunctionalInterface
	public static interface ImageChangeHandler {
		void onImageChange(PImage image);
	}
	
	public static enum ICON_NAMES {
		SPRITE("sprite"),
		PURPLE("purple"),
		TRIANGLE("triangle"),
		GRADIENT_TRIANGLE("gradient-triangle"),
		SWIRL("swirl"),
		WARNING("warning"),
		THREE_D_CUBE("3dcube"),
		SILLY("Silly_Emoji"),
		SCARED("Scared_face_emoji"),
		ISLAND("emoji-island"),
		BLITZ("Emoji_Blitz_Star"),
		SIMPLE_CIRCLE("simpleCircle"),
		THREE_D_STAR("3dstar");
		
		private String title;
		private ICON_NAMES(String title) {
			this.title = title;
		}
		
		public String getFullTitle() {
			return title + ".png";
		}
		
		public static final List<ICON_NAMES> VALUES = Arrays.asList(ICON_NAMES.values());
		public static final int SIZE = VALUES.size();
		private static final Random RANDOM = new Random();

		public static ICON_NAMES random() {
			return VALUES.get(RANDOM.nextInt(SIZE));
		}
	}
	
	private Map<ICON_NAMES, List<ImageChangeHandler>> handlers = new HashMap<ICON_NAMES, List<ImageChangeHandler>>();
	private Map<ICON_NAMES, String> alternateIconMap = new HashMap<ICON_NAMES, String>();
	
	private List<PImage> images = new ArrayList<PImage>();
	
	public ImageHelper(Main parent) {
		super(parent);
	}
	
	public Map<ICON_NAMES, String> getAlternateMap() {
		return alternateIconMap;
	}
	
	public void dispose(PGraphics pg) {
		for (PImage img : images) {
			Object cache = pg.getCache(img);
			if (cache instanceof Texture) {
				((Texture) cache).disposeSourceBuffer();
			}
			pg.removeCache(img);
			
		}
		images.clear();
	}

	public PImage loadImage(ICON_NAMES icon, String path, ImageChangeHandler handler) {
		List<ImageChangeHandler> list = handlers.get(icon);
		if (list == null) {
			list = new ArrayList<ImageChangeHandler>();
			handlers.put(icon, list);
		}
		list.add(handler);
		
		if (!icon.getFullTitle().equalsIgnoreCase(path)) {
			alternateIconMap.put(icon, path);
		}
		
		PImage result = parent.loadImage(path);
		images.add(result);
		return result;
	}

	public void updateImage(ICON_NAMES icon, PImage image, String fileName) {
		List<ImageChangeHandler> list = handlers.get(icon);
		if (list != null) {
			list.forEach(h -> h.onImageChange(image));
		}
		alternateIconMap.put(icon, fileName);
	}

	@Override
	public String getConfig() {
		StringBuffer buffer = new StringBuffer(CONFIG_HEADER);
		ICON_NAMES.VALUES.forEach(icon -> {
			String pathName = icon.getFullTitle();
			if (alternateIconMap.containsKey(icon)) {
				pathName = alternateIconMap.get(icon);
			}
			
			buffer.append(String.format("%s = %s", icon.getFullTitle(), pathName)); 
			buffer.append(System.lineSeparator());
		});
		
		buffer.append(System.lineSeparator());
		return buffer.toString();
	}
	
}

