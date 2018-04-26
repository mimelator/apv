package com.arranger.apv.archive;

import java.io.File;
import java.util.Random;

import com.arranger.apv.util.FileHelper;

public class FileWatcherTest /*extends APVPluginTest*/ {
	
	private static final String FILE_TO_WATCH = "test.txt";
	private boolean changed = false;
	private int target;
	
	public FileWatcherTest() {
	}

	
	public void testFileWatcher() throws Exception {
		FileHelper fh = new FileHelper(null);
		final File file = new File(fh.getFullPath(FILE_TO_WATCH));
		System.out.println("watching: " + file.getAbsolutePath());
		new FileWatcher(file, () -> {
			changed = true;
		}).start();
		
		Thread.yield();
		target = new Random().nextInt();
		fh.saveFile(FILE_TO_WATCH, "Hello There World: " + target);
		
		while (!changed) {
			Thread.sleep(1000);
		}
		
		String contents = fh.readFile(FILE_TO_WATCH);
		assert(contents != null);
		assert(!contents.isEmpty());
		int index = contents.indexOf(String.valueOf(target));
		assert(index != -1);
	}
	
	
//	@Override
//	protected void setFrameIndexes() {
//
//	}

}
