package com.arranger.apv.util;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * https://stackoverflow.com/questions/16251273/can-i-watch-for-single-file-change-with-watchservice-not-the-whole-directory
 */
public class FolderWatcher extends Thread {
	
	@FunctionalInterface
	public static interface FolderChangeWatcher {
		public void onFolderChange();
	}
	
    private final File folder;
    private final FolderChangeWatcher watcher;
    private AtomicBoolean stop = new AtomicBoolean(false);

    public FolderWatcher(File file, FolderChangeWatcher watcher) {
        this.folder = file;
        this.watcher = watcher;
    }

    public boolean isStopped() { return stop.get(); }
    public void stopThread() { stop.set(true); }

    public void doOnChange() {
    	watcher.onFolderChange();
    }

    @Override
    public void run() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            Path path = folder.toPath();
            path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            while (!isStopped()) {
                WatchKey key;
                try { key = watcher.poll(25, TimeUnit.MILLISECONDS); }
                catch (InterruptedException e) { return; }
                if (key == null) { Thread.yield(); continue; }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    //@SuppressWarnings("unchecked")
                    //WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    //Path filename = ev.context();

                    if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_CREATE) {
                        doOnChange();
                    }
                    boolean valid = key.reset();
                    if (!valid) { break; }
                }
                Thread.yield();
            }
        } catch (Throwable e) {
            // Log or rethrow the error
        }
    }
}