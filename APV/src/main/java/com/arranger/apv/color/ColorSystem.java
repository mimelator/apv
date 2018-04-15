package com.arranger.apv.color;

import java.awt.Color;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public abstract class ColorSystem extends APVPlugin {

	public ColorSystem(Main parent) {
		super(parent);
	}

	public abstract Color getCurrentColor();


}
