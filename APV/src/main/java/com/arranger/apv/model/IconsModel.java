package com.arranger.apv.model;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import com.arranger.apv.Main;
import com.arranger.apv.db.entity.ImageEntity;
import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.ImageHelper.ICON_NAMES;

import processing.core.PImage;

public class IconsModel extends APVModel {

	public static final Dimension PREFERRED_ICON_SIZE = new Dimension(400, 400);
	
	private Map<ICON_NAMES, ImageHolder> iconMap = new HashMap<ICON_NAMES, ImageHolder>();
	
	public IconsModel(Main parent) {
		super(parent);
		reset();
	}
	
	protected void init() {
		ICON_NAMES.VALUES.forEach(icon -> {
			String iconFile = parent.getConfigString(icon.getFullTitle());
			Image img = parent.loadImage(iconFile).getImage();
			ImageHolder imageHolder = new ImageHolder(iconFile, img);
			checkAlpha(imageHolder);
			iconMap.put(icon, imageHolder);
		});
		
		//overlay the defaults with the alternates
		Map<ICON_NAMES, String> map = parent.getImageHelper().getAlternateMap();
		map.entrySet().forEach(entry -> {
			ICON_NAMES name = entry.getKey();
			String path = entry.getValue();
			iconMap.get(name).setFile(new File(path));
		});
	}
	
	public Map<ICON_NAMES, ImageHolder> getIconMap() {
		return iconMap;
	}

	@Override
	public void reset() {
		parent.getSetupEvent().register(() -> {
			init();
		});
	}
	
	@Override
	public void randomize() {
		//Do nothing
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public void loadFromEntities(List entities) {
		FileHelper fh = new FileHelper(parent);
		getIconMap().values().forEach(ih -> {
			ImageEntity ie = (ImageEntity)getRandomEntity(entities);
			String fullPath = fh.getFullPath(ie.getFilename());
			ih.setFile(new File(fullPath));
		});
	}

	public void checkAlpha(ImageHolder imageHolder) {
		boolean hasAlpha = false;
		Image image = imageHolder.getImage();
		
		if (image instanceof BufferedImage) {
			hasAlpha = ((BufferedImage)image).getColorModel().hasAlpha();
		}
		
		imageHolder.setHasAlpha(hasAlpha);
	}

	public class ImageHolder {
		private File file;
		private Image image, origImage;
		private boolean hasAlpha = true;
		
		public ImageHolder(String configPath, Image image) {
			this.image = image;
			this.origImage = image;
		}
		
		public File getFile() {
			return file;
		}
		
		public void setFile(File file) {
			this.file = file;
			image = null;
		}
		
		public void setHasAlpha(boolean hasAlpha) {
			this.hasAlpha = hasAlpha;
		}

		public boolean isHasAlpha() {
			return hasAlpha;
		}

		public void setImage(Image image) {
			this.image = image;
		}

		public Image getImage() {
			if (image == null) {
				image = parent.loadImage(file.getAbsolutePath()).getImage();
			}
			return image;
		}
		
		public PImage getOriginalImage() {
			return new PImage(origImage);
		}
		
		public PImage getPImage() {
			return new PImage(getImage());
		}
		
		public ImageIcon getImageIcon() {
			Image img = getImage();
			
			//scale it
			Image scaled = img.getScaledInstance(PREFERRED_ICON_SIZE.width, PREFERRED_ICON_SIZE.height, Image.SCALE_SMOOTH);
			return new ImageIcon(scaled);
		}
	}
}
