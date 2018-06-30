package com.arranger.apv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.FolderWatcher;

public class ProcessControl {
	
	private Queue<File> queue = new ConcurrentLinkedQueue<File>();
	private File setPackFolder;
	private Main main;
	
	private void populateQueue() {
		File[] listFiles = setPackFolder.listFiles(f -> f.isDirectory());
		for (File f : listFiles) {
			queue.offer(f);
		}
	}
	
	private void consumeQueue() {
		File spf = queue.poll();
		String confFile = spf.toPath().resolve("application.conf").toAbsolutePath().toString();
		startAPV(confFile);
	}
	
	private void watchFolder() {
		//Watch folder
		new FolderWatcher(setPackFolder, () -> populateQueue()).start();
	}
	
	private ProcessControl() {
		main = new Main(true);
		main.settings();
		
		setPackFolder = new FileHelper(main).getSetPacksFolder();
		populateQueue();
		watchFolder();
		
		while (!queue.isEmpty()) {
			consumeQueue();
		}
	}

	private int startAPV(String file) {
		List<String> cmds = new ArrayList<String>();
		cmds.add("java");
		
		//Flags
		Main.FLAGS.VALUES.forEach(flag -> {
			String property = System.getProperty(flag.apvName());
			if (property != null) {
				StringBuffer buffer = new StringBuffer("-D");
				buffer.append(flag.apvName()).append('=');
				buffer.append(property);
				cmds.add(buffer.toString());
			}
		});
		
		//ClassPath
		String cp = System.getProperty("java.class.path");
		cmds.add("-Djava.class.path=" + cp);
		
		
		//Override default commands and set config file
		cmds.add("-DdefaultCommands.0");
		cmds.add("-Dconfig.file=" + file);
		
		cmds.add(Main.class.getName());
		
		//start it
		try {
			ProcessBuilder pb = new ProcessBuilder().command(cmds).inheritIO();
			Process apvProc = pb.start();
			return apvProc.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static void main(String[] args) {
		new ProcessControl();
	}
}
