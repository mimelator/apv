package com.arranger.apv.util;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

import processing.event.MouseEvent;

public class MouseListener extends APVPlugin {

	public MouseListener(Main parent) {
		super(parent);
		parent.registerMethod("mouseEvent", this);
	}

	public void mouseEvent(MouseEvent mouseEvent) {
		if (mouseEvent.getAction() == MouseEvent.CLICK) {
			parent.getMousePulseEvent().fire();
		}
	}
}
