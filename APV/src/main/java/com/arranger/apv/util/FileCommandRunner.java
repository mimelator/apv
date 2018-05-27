package com.arranger.apv.util;

import java.io.File;
import java.util.Arrays;

import com.arranger.apv.Main;


public class FileCommandRunner extends StartupCommandRunner {

	public static final String FILE_KEY = "watchedCommandFile";
	
	public FileCommandRunner(Main parent) {
		super(parent);
		
		String fileToWatch = parent.getConfigString(FILE_KEY);
		FileHelper fh = new FileHelper(parent);
		File file = new File(fh.getFullPath(fileToWatch));
		
		//set up a file watcher
		System.out.println("watching: " + file.getAbsolutePath());
		new FileWatcher(file, () -> {
			runCommands(file);
		}).start();
	}
	
	protected void runCommands(File file) {
		FileHelper fh = new FileHelper(parent);
		String commandStrings = fh.readFile(file.toPath());
		String[] split = commandStrings.split("[\\r\\n]+");
		runCommands(Arrays.asList(split));
	}

	@Override
	public String getConfig() {
		return FILE_KEY + " = \"" + parent.getConfigString(FILE_KEY) + "\"";
	}
	
}
