package com.arranger.apv.test;

import org.junit.jupiter.api.Test;

import com.arranger.apv.Command;

public class CommandTest extends APVPluginTest {

	public CommandTest() {
	}

	@Test
	public void touchCommand() {
		Command audioDec = Command.AUDIO_DEC; //There is a static checker to see if we have duplicate command keys
		assert(audioDec != null);
		System.out.println("No duplicate Commands");
	}
	
	
	@Override
	protected void setFrameIndexes() {

	}

}
