package com.arranger.apv.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.model.IconsModel.ImageHolder;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.ImageHelper;
import com.arranger.apv.util.ImageHelper.ICON_NAMES;

import processing.core.PImage;

public class Creator extends APVPlugin {

	public static final String SONGS_DIR = "songs";
	public static final String SET_PACK_HOME_KEY = "apv.setPack.home";
	
	private static final Logger logger = Logger.getLogger(Creator.class.getName());

	public Creator(Main parent) {
		super(parent);
	}
	
	@FunctionalInterface
	public static interface UpdateCallback {
		public void onUpdateForDemo(boolean isDemoActive, Path parentDirectory);
	}
	
	@FunctionalInterface
	public static interface CreateFilesCallback {
		public void onCreateFilesForSetPack(Path parentDirectory);
	}
	
	public void createSetPack(File parentDirectory, String setPackName, UpdateCallback uc, CreateFilesCallback cc) throws IOException {
		Path parentDirectoryPath = parentDirectory.toPath();
		parentDirectoryPath = parentDirectoryPath.resolve(setPackName);
		Files.createDirectories(parentDirectoryPath);
		final Path parentFolderPath = parentDirectoryPath;
		
		uc.onUpdateForDemo(true, parentFolderPath);
		
		String referenceText = parent.getConfigurator().generateCurrentConfig();
		Path referencePath = parentDirectoryPath.resolve(Configurator.APPLICATION_CONF);
		
		String instructions = String.format("#java -Dconfig.file=%s/application.conf -jar apv.jar", setPackName) + System.lineSeparator();
		String setPackHome = String.format("%s = %s", SET_PACK_HOME_KEY, parentDirectory.getAbsolutePath()) + System.lineSeparator();

		String result = String.format("%s %s %s", instructions, setPackHome, referenceText);
		Files.write(referencePath, result.getBytes());
		
		cc.onCreateFilesForSetPack(parentFolderPath);
	}
	
	public void setSongsRelativeDir(Path parentDirectory) {
		Path relativeSongsDir = parentDirectory.getFileName().resolve(SONGS_DIR);
		parent.getSetListPlayer().setRelativeConfigDirectory(relativeSongsDir.toString());
	}

	public void createSongFilesForSetPack(Path parentDirectory, boolean preserveOrder, List<File> songList) throws IOException {
		
		//copy all of the items in the song list to the parentDirectory
		Path songFolder = parentDirectory.resolve(Creator.SONGS_DIR);
		Files.createDirectories(songFolder);
		
		IntStream.range(0, songList.size()).forEach(i -> {
			File songFile = songList.get(i);
			Path srcPath = songFile.toPath();
			String name = songFile.getName();
			
			if (preserveOrder) {
				name = String.format("%03d_%s", i, name);
			}
			
			Path destPath = songFolder.resolve(name);
			try {
				Files.copy(srcPath, destPath);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		});
	}
	
	public void updateIconsForDemo(boolean isDemoActive, Path parentDirectory) {
		IconsModel iconsModel = parent.getIconsModel();
		ImageHelper ih = parent.getImageHelper();
		Map<ICON_NAMES, ImageHolder> iconMap = iconsModel.getIconMap();
		ICON_NAMES.VALUES.forEach(icon -> {
			ImageHolder imgHolder = iconMap.get(icon);
			
			PImage image = isDemoActive ? imgHolder.getPImage() : imgHolder.getOriginalImage();
			String pathName = icon.getFullTitle();
			if (isDemoActive && imgHolder.getFile() != null && parentDirectory != null) {
				pathName = "${apv.setPack.home}" + File.separator + parentDirectory.getFileName() + File.separator + imgHolder.getFile().getName();
			}
			
			ih.updateImage(icon, image, pathName);
		});
	}
	
	public void createIconFilesForSetPack(Path parentDirectory) throws IOException {
		IconsModel iconsModel = parent.getIconsModel();
		Map<ICON_NAMES, ImageHolder> iconMap = iconsModel.getIconMap();
		ICON_NAMES.VALUES.forEach(icon -> {
			ImageHolder imgHolder = iconMap.get(icon);
			if (imgHolder.getFile() != null) {
				Path srcPath = imgHolder.getFile().toPath();
				Path destPath = parentDirectory.resolve(imgHolder.getFile().getName());
				try {
					Files.copy(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		});
	}
	
}
