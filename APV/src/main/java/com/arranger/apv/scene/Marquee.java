package com.arranger.apv.scene;

import com.arranger.apv.Main;
import com.arranger.apv.Scene;

public class Marquee extends Scene {

	public Marquee(Main parent) {
		super(parent);
	}

	@Override
	public boolean isNormal() {
		return false;
	}

	@Override
	public void drawScene() {
		parent.text("Hello World", parent.width / 2, parent.height / 2);
	}

	
}
