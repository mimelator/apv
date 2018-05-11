package com.arranger.apv.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class FileHelper extends APVPlugin {
	
	private static final String MUSIC_DIR = "Music";

	private static final Logger logger = Logger.getLogger(FileHelper.class.getName());
	
	public static final String HOME_DIR = System.getProperty("user.home");
	public static final String APV_DIR = HOME_DIR + File.separator + "apv";
	public static final String APV_MUSIC_DIR = "apv.musicDir";
	
	private File rootFolder;

	public FileHelper(Main parent)  {
		super(parent);
		
		try {
			rootFolder = new File(APV_DIR);
			rootFolder.mkdirs();
		} catch (Exception e) {
			debug(e);
		}
	}

	public String getFullPath(String fileName) {
		try {
			return rootFolder.getCanonicalPath() + File.separator + fileName;
		} catch (Exception e) {
			debug(e);
			return fileName;
		}
	}
	
	public boolean saveFile(String fileName, String text) {
		return saveFile(fileName, text, false);
	}
	
	public boolean saveFile(String fileName, String text, boolean append) {
		try {
			String fullPathString = fileName;
			if (!new File(fileName).isAbsolute()) {
				fullPathString = getFullPath(fileName);
			}
			
			Path fullPath = Paths.get(fullPathString);
			byte[] bytes = text.getBytes();
			if (append) {
				Files.write(fullPath, bytes, StandardOpenOption.APPEND, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
			} else {
				Files.write(fullPath, bytes); //StandardOpenOption.WRITE, StandardOpenOption.CREATE);
			}
		} catch (Exception e) {
			debug(e);
			return false;
		}
		return true;
	}
	
	public String readFile(String fileName) {
		String result = null;
		try {
			String fullPath = getFullPath(fileName);
			Path p = Paths.get(fullPath);
			result = new String(Files.readAllBytes(p));
		} catch (Exception e) {
			debug(e);
		}
		return result;
	}
	
	@FunctionalInterface
	public static interface StreamConsumer {
		public void consumeInputStream(InputStream is) throws Exception;
	}
	
	public void getResourceAsStream(String resource, StreamConsumer handler) {
		InputStream inputStream = null;
		try {
			logger.info("Loading resource: " + resource);
			inputStream = getClass().getClassLoader().getResourceAsStream(resource);
		    assert(inputStream != null);
		    handler.consumeInputStream(inputStream);
		} catch (Exception e) {
			debug(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ioe) {
					debug(ioe);
				}
			}
		}
	}
	
	private JFileChooser fileChooser;
	private File directory;
	
	public JFileChooser getJFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			
			fileChooser.addActionListener(evt -> {
				if (evt.getActionCommand() == JFileChooser.APPROVE_SELECTION) {
					directory = fileChooser.getCurrentDirectory();
				}
			});
			
		}
		
		if (directory != null) {
			fileChooser.setCurrentDirectory(directory);
		}
		
		return fileChooser;
	}
	
	public Path getFirstMp3FromMusicDir() {
		
		Path musicPath = null;
		String musicDir = parent.getConfigurator().getRootConfig().getString(APV_MUSIC_DIR);
		if (musicDir == null || musicDir.isEmpty()) {
			musicPath = new File(HOME_DIR).toPath().resolve(MUSIC_DIR);
		} else {
			musicPath = new File(musicDir).toPath();
		}
		
		Path result = null;
		
		try {
			result = Files.find(musicPath, 4, (p, bfa) -> p.toString().endsWith(".mp3")).findFirst().get();
		} catch (IOException e) {
			debug(e);
		}
		return result;
	}
	
	private void debug(Exception e) {
		logger.log(Level.SEVERE, e.getMessage(), e);
	}
}
