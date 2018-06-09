package com.arranger.apv.archive;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class RestartManager extends APVPlugin {

	public RestartManager(Main parent) {
		super(parent);
	}
	
	/**
	 * I'd like to keep most things similar:
	 * 	java -Dconfig.file=Omega3/application.conf -jar apv.jar
	 * @param setpackFile
	 */
	public void restartWithSetPackFile(String setpackFile) {
		StringBuilder cmd = new StringBuilder();
        cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cmd.append(jvmArg + " ");
        }
        cmd.append("-Dconfig.file=").append(setpackFile).append(" ");
        cmd.append(Main.class.getName());
    
        // FORK-BOMB ALERT
        System.out.println("Attempting to run: ");
        System.out.println(cmd.toString());
        try {
        	Runtime.getRuntime().exec(cmd.toString());
        } catch (IOException e) {
			e.printStackTrace();
		}
        System.exit(0);
	}
	
	/**
	 * https://stackoverflow.com/questions/4159802/how-can-i-restart-a-java-application
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
        StringBuilder cmd = new StringBuilder();
        cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cmd.append(jvmArg + " ");
        }
        cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
        cmd.append(Main.class.getName()).append(" ");
        for (String arg : args) {
            cmd.append(arg).append(" ");
        }
        // FORK-BOMB ALERT
        //Runtime.getRuntime().exec(cmd.toString());
        System.exit(0);
    }

}
