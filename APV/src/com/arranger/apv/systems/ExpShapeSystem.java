package com.arranger.apv.systems;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;
import com.arranger.apv.ShapeSystem;

import processing.core.PConstants;

public class ExpShapeSystem extends ShapeSystem implements PConstants {

	public ExpShapeSystem(Main parent, ShapeFactory factory) {
		super(parent, factory);
	}

	@Override
	public void setup() {
	}
	
	@Override
	public void draw() {
		parent.color(Color.BLUE.getRGB());
		parent.ellipse(parent.width / 2, parent.height / 2, 100, 100);
	}
}
