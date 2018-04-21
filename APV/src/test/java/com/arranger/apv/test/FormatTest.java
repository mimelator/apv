package com.arranger.apv.test;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FormatTest {

	long startTime;
	
	@BeforeEach
	void setUp() throws Exception {
		startTime = System.currentTimeMillis();
	}

	@Test
	void test() {
		
		String result = format(Color.RED);
		System.out.println(result);
		assert(result != null);
		
		String timeStamp = format(System.currentTimeMillis() - startTime);
		System.out.println(timeStamp);
	}
	
	/**
	 * https://stackoverflow.com/questions/9027317/how-to-convert-milliseconds-to-hhmmss-format
	 */
	private String format(long millis) {
		return String.format("%02d:%02d:%02d", 
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
				TimeUnit.MILLISECONDS.toSeconds(millis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));   
	}
	
	private String format(Color c) {
		return String.format("(%1s,%2s,%3s)", c.getRed(), c.getGreen(), c.getBlue());
	}

}
