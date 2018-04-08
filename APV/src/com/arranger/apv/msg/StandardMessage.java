package com.arranger.apv.msg;

import java.util.logging.Logger;

import com.arranger.apv.Main;
import com.arranger.apv.MessageSystem;

public class StandardMessage extends MessageSystem {

	private static final Logger logger = Logger.getLogger(StandardMessage.class.getName());
	
	public StandardMessage(Main parent) {
		super(parent, null);
	}

	@Override
	public void setup() {

	}

	@Override
	public void draw() {
		logger.fine("frameCount: " + parent.getFrameCount());
	}
	
}
