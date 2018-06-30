package com.arranger.apv;

import java.util.ArrayList;
import java.util.List;

public class ProcessControl {

	

	public static void main(String[] args) {
		start("/Users/markimel/apv/oceans/monday/setPacks/pitstop/application.conf");
		start("/Users/markimel/apv/oceans/monday/setPacks/Pink/application.conf");
		start("/Users/markimel/apv/oceans/monday/setPacks/Rush/application.conf");
	}

	protected static int start(String file) {
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
			} else {
				//System.out.println("Null value for flag: " + flag.apvName());
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
}
