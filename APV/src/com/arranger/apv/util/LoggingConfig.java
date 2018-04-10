package com.arranger.apv.util;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class LoggingConfig extends APVPlugin {

	private static final boolean DEBUG_LOG_CONFIG = false;
	private static final String CONFIG = "/config/log.properties";
	
	public LoggingConfig(Main parent) {
		super(parent);
	}

	public void configureLogging()  {
		LogManager logManager = LogManager.getLogManager();
		logManager.reset();
		try {
			InputStream configFile = Main.class.getResourceAsStream(CONFIG);
			logManager.readConfiguration(configFile);
			configFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Dump the loggers
		if (DEBUG_LOG_CONFIG) {
			Enumeration<String> loggerNames = logManager.getLoggerNames();
			while (loggerNames.hasMoreElements()) {
				debugLogger(logManager.getLogger(loggerNames.nextElement()));
			}
		}
	}
	
	protected void debugLogger(Logger l) {
		Level level = l.getLevel();
		String name = l.getName();
		System.out.println("name: " + name + " level: " + level);
		
		l = l.getParent();
		int indent = 1;
		while (l != null) {
			for (int index = 0; index < indent; index++) {
				System.out.print("   ");
			}
			level = l.getLevel();
			name = l.getName();
			System.out.println("name: " + name + " level: " + level);
			l = l.getParent();
			indent++;
		}
	}
}
