package com.arranger.apv.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Experimenting with logging
 * @author markimel
 *
 */
public class LogTest {
	
	private static final Logger LOG = Logger.getLogger(LogTest.class.getName());
	private static final boolean DEBUG_LOG_CONFIG = false;
	

	public LogTest() {
	}

	@BeforeEach
	public void configLogger() {
		LogManager logManager = LogManager.getLogManager();
		try {
			System.out.println("Loading log properties");
		    InputStream configFile = LogTest.class.getResourceAsStream("/config/log.properties");
		    assert(configFile != null);
			logManager.readConfiguration(configFile);
			
		} catch (IOException ex) {
		    System.out.println(ex.getMessage());
		    ex.printStackTrace();
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
		
		for (Handler handler : l.getHandlers()) {
			Level handlerLevel = handler.getLevel();
			String handlerName = handler.getClass().getName();
			Formatter formatter = handler.getFormatter();
			String formatterName = formatter.getClass().getName();
			
			System.out.printf("  Handler: [name, level, formatter]: %1s, %2s, %3s\n", handlerName, handlerLevel.getName(), formatterName);
		}
		
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
		
	@Test
	public void testLogging() {
		LOG.log(Level.FINE, "message starts with text: FINE");
		LOG.log(Level.INFO, "message starts with text: INFO");
		LOG.log(Level.WARNING, "message starts with text: WARNING");
		LOG.log(Level.SEVERE, "message starts with text: SEVERE");
		LOG.log(Level.CONFIG, "message starts with text: CONFIG");
		OtherClass.otherClassTest();
	}
	
	public static class OtherClass {
		private static final Logger OTHER_LOGGER = Logger.getLogger(OtherClass.class.getName());
		
		public static void otherClassTest() {
			OTHER_LOGGER.log(Level.FINE, "message starts with text: FINE");
			OTHER_LOGGER.log(Level.INFO, "message starts with text: INFO");
			OTHER_LOGGER.log(Level.WARNING, "message starts with text: WARNING");
			OTHER_LOGGER.log(Level.SEVERE, "message starts with text: SEVERE");
			OTHER_LOGGER.log(Level.CONFIG, "message starts with text: CONFIG");
		}
		
	}
}
