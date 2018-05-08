package com.arranger.apv.test;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import com.arranger.apv.util.ReflectionHelper;

public class ReflectionHelperTest extends APVPluginTest {

	public ReflectionHelperTest() {
	}

	@Test
	public void testReflectionHelper() {
		ReflectionHelper<Color, Color> col = new ReflectionHelper<Color, Color>(Color.class, parent);
		Color black = col.getField("BLACK");
		assert(black != null);
		assert(Color.BLACK.equals(black));
	}
	
	
	@Override
	protected void setFrameIndexes() {

	}

}
