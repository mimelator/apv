package com.arranger.apv.menu;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class StandardMenu extends APVPlugin {

	public StandardMenu(Main parent) {
		super(parent);
	}

	public void draw() {
		parent.fill(0);
		parent.rect(0, 0, parent.width, parent.height);
	}
}
