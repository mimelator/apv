package com.arranger.apv.test;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FormatTest {

	long millis;
	
	@BeforeEach
	void setUp() throws Exception {
		millis = System.currentTimeMillis();
	}

	@Test
	void test() {
		
		
		String result = format(Color.RED);
		System.out.println(result);
		assert(result != null);
		
		int n = (int)(System.currentTimeMillis() - millis);
		System.out.println(String.format("%1s", n));
	}
	
	private String format(Color c) {
		return String.format("(%1s,%2s,%3s)", c.getRed(), c.getGreen(), c.getBlue());
	}

}
