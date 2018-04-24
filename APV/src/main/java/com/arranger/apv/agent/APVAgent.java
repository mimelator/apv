package com.arranger.apv.agent;

import com.arranger.apv.Main;
import com.arranger.apv.helpers.APVCallbackHelper;

public class APVAgent extends APVCallbackHelper {

	public APVAgent(Main parent) {
		super(parent, Main.SYSTEM_NAMES.AGENTS);
	}

}
