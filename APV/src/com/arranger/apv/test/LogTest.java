package com.arranger.apv.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.arranger.apv.Main;

/**
 * Experimenting with logging
 * @author markimel
 *
 */
public class LogTest {
	
	private static final Logger LOG = Logger.getLogger(LogTest.class.getName());
	

	public LogTest() {
	}

	@BeforeEach
	public void configLogger() {
		try {
			System.out.println("Loading log properties");
		    InputStream configFile = LogTest.class.getResourceAsStream("/config/log.properties");
		    assert(configFile != null);
		    LogManager.getLogManager().readConfiguration(configFile);
		} catch (IOException ex) {
		    System.out.println(ex.getMessage());
		    ex.printStackTrace();
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
		private static final Logger OTHER_LOGGER = Logger.getLogger(Main.class.getName());
		
		public static void otherClassTest() {
			OTHER_LOGGER.log(Level.FINE, "message starts with text: FINE");
			OTHER_LOGGER.log(Level.INFO, "message starts with text: INFO");
			OTHER_LOGGER.log(Level.WARNING, "message starts with text: WARNING");
			OTHER_LOGGER.log(Level.SEVERE, "message starts with text: SEVERE");
			OTHER_LOGGER.log(Level.CONFIG, "message starts with text: CONFIG");
		}
		
	}
}
