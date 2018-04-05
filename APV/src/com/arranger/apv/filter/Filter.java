package com.arranger.apv.filter;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class Filter extends APVPlugin {

	public Filter(Main parent) {
		super(parent);
	}
	
	/**
	 * Any matrix or style set MUST be undone in {@link #postRender()}
	 */
	public void preRender() {
		parent.pushStyle();
		parent.pushMatrix();
	}
	
	/**
	 * Any matrix or style set done in {@link #preRender()} MUST be undone
	 */
	public void postRender() {
		parent.popMatrix();
		parent.popStyle();
	}
	
}
