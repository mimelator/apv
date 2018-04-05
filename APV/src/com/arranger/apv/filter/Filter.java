package com.arranger.apv.filter;

import com.arranger.apv.Main;

public class Filter {

	protected Main parent;

	public Filter(Main parent) {
		this.parent = parent;
	}
	
	public void preRender() {}
	public void postRender() {}
	
}
