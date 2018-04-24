package com.arranger.apv.test;

import org.junit.jupiter.api.Test;

import com.arranger.apv.util.RuntimeHelper;

public class RuntimeHelperTest extends APVPluginTest {

	public RuntimeHelperTest() {
	}
	
	@Test
	public void getThreadDump() {
		
		RuntimeHelper rh = new RuntimeHelper(parent);
		String threadDump = rh.generateThreadDump();
		assert(threadDump != null);
		assert(!threadDump.isEmpty());
		
		int index = threadDump.indexOf(getClass().getName());
		assert(index != -1);
		debug("Found evidence at: " + index);
	}

	@Override
	protected void setFrameIndexes() {
	}

}
