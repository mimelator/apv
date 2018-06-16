package com.arranger.apv.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

public class ArrayManipTest extends APVPluginTest {
	
	private static final String TEST_COMMAND_STRING = "LIVE_SETTINGS:FLAG:TREE_MIN_SIZE";

	public ArrayManipTest() {
	}

	@Override
	protected void setFrameIndexes() {
	}

	@Test
	public void testArrayManip() {
		String cmdString = TEST_COMMAND_STRING;
		
		String primaryArg = null;
		String [] args = null;
		
		if (cmdString.contains(":")) {
			String[] split = cmdString.split(":");
			cmdString = split[0];
			primaryArg = split[1];
			if (split.length > 2) {
				int newLength = split.length - 2;
				args = new String[newLength];
				System.arraycopy(split, 2, args, 0, newLength);
			}
		}
		
		assertTrue(primaryArg != null);
		assertTrue(args != null);
		
		assertTrue(primaryArg.equals("FLAG"));
		assertTrue(args.length == 1);
		assertTrue(args[0].equals("TREE_MIN_SIZE"));
	}
}
