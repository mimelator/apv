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

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class FileHelper extends APVPlugin {
	
	private static final Logger logger = Logger.getLogger(FileHelper.class.getName());
	
	private static final String HOME_DIR = System.getProperty("user.home");
	public static final String APV_DIR = HOME_DIR + File.separator + "apv";
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
			String fullPath = getFullPath(fileName);
			if (append) {
				Files.write(Paths.get(fullPath), 
						text.getBytes(), 
						StandardOpenOption.APPEND, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
			} else {
				Files.write(Paths.get(fullPath), 
						text.getBytes(), 
						StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
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
	
	private void debug(Exception e) {
		logger.log(Level.SEVERE, e.getMessage(), e);
	}
}
