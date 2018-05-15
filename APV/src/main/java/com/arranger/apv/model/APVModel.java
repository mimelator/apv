package com.arranger.apv.model;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public abstract class APVModel extends APVPlugin {

	public APVModel(Main parent) {
		super(parent);
	}

	public abstract void reset();
	public abstract void randomize();
}
