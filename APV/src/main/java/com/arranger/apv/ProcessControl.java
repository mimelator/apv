package com.arranger.apv;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.FolderWatcher;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

public class ProcessControl {

	public static String[] UNIQUE_FLAGS = { "defaultCommands", "disabledPlugins", "apvMessages" }; // could include all
																									// APV Systems

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
		// Watch folder
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

			if (queue.isEmpty()) {
				populateQueue();
			}
		}
	}

	private int startAPV(String file) {
		List<String> cmds = new ArrayList<String>();
		cmds.add("java");

		// Flags
		Main.FLAGS.VALUES.forEach(flag -> {
			String property = System.getProperty(flag.apvName());
			if (property != null) {
				StringBuffer buffer = new StringBuffer("-D");
				buffer.append(flag.apvName()).append('=');
				buffer.append(property);
				cmds.add(buffer.toString());
			}
		});

		// ClassPath
		String cp = System.getProperty("java.class.path");
		cmds.add("-Djava.class.path=" + cp);

		// Override default commands and set config file
		cmds.addAll(getAdditionalCommands());
		cmds.add("-Dconfig.file=" + file);

		cmds.add(Main.class.getName());

		// start it
		try {
			ProcessBuilder pb = new ProcessBuilder().command(cmds).inheritIO();
			Process apvProc = pb.start();
			return apvProc.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	private List<String> getAdditionalCommands() {

		Config config = ConfigFactory.load();
		List<String> cmds = new ArrayList<String>();

		IntStream.range(0, UNIQUE_FLAGS.length).forEach(i -> {
			String uf = UNIQUE_FLAGS[i];
			if (config.hasPath(uf)) {
				Iterator<ConfigValue> iterator = null;

				ConfigValue value = config.getValue(uf);
				if (value instanceof ConfigList) {
					ConfigList cl = (ConfigList) value;
					iterator = cl.iterator();
				} else {
					ConfigObject configObject = config.getObject(uf);
					Collection<ConfigValue> values = configObject.values();
					iterator = values.iterator();
				}

				for (int configIndex = 0; iterator.hasNext(); configIndex++) {
					ConfigValue next = iterator.next();
					String format = String.format("-D%s.%s=%s", uf, configIndex, next.unwrapped());
					cmds.add(format);
				}
			}
		});

		return cmds;
	}

	public static void main(String[] args) {
		new ProcessControl();
	}
}
