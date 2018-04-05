package com.arranger.apv;

import processing.core.PConstants;

public abstract class APVPlugin implements PConstants {

	protected Main parent;

	public APVPlugin(Main parent) {
		this.parent = parent;
	}
}
