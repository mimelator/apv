package com.arranger.apv.test;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FormatTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void test() {
		String result = format(Color.RED);
		System.out.println(result);
		assert(result != null);
	}
	
	private String format(Color c) {
		return String.format("(%1s,%2s,%3s)", c.getRed(), c.getGreen(), c.getBlue());
	}

}
