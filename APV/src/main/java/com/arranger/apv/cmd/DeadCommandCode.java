package com.arranger.apv.cmd;

public class DeadCommandCode {

	private static final int DEAD_COMMAND_CODE = 0x2000;
	private static int index = 0;
	
	public static int next() {
		return DEAD_COMMAND_CODE + index++;
	}
	
}
