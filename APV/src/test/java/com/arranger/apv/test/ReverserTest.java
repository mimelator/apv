package com.arranger.apv.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.arranger.apv.util.Reverser;


public class ReverserTest extends APVPluginTest {
	
	private Reverser reverser;

	@BeforeEach
	public void beforeEach() {
		super.beforeEach();
		debug("ReverserTest#beforeEach");
		reverser = new Reverser(parent, 2);	//cycle time of 2
	}

	@Test
	public void testReverserTest() throws Exception {
		debug("ReverserTest");
		assert(reverser != null);
		
		boolean curReverse = reverser.isReverse();
		reverser.setReverse(!curReverse);
		assert(reverser.isReverse() == !curReverse);
		debug("1) reverse: " + !curReverse);
		
		//the next frame should NOT reverse it
		advanceFrame();
		assert(reverser.isReverse() == !curReverse);
		
		//the next frame should reverse it back
		advanceFrame();
		assert(reverser.isReverse() == curReverse);
		debug("2) reverse: " + reverser.isReverse());
		
		//the next frame should NOT reverse it
		advanceFrame();
		assert(reverser.isReverse() == curReverse);
	}

	@Override
	protected void setFrameIndexes() {
		this.frameIndexStart = 0;
		this.frameIndexEnd = 50;
	}
}
