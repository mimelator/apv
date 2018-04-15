package com.arranger.apv.util;

import java.io.File;
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
			e.printStackTrace();
			logger.severe(e.getMessage());
		}
	}

	public String getFullPath(String fileName) {
		try {
			return rootFolder.getCanonicalPath() + File.separator + fileName;
		} catch (Exception e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
			return fileName;
		}
	}
}
