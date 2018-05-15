package com.arranger.apv.shader;

import java.util.List;

import com.arranger.apv.Main;

import processing.core.PImage;

public class DynamicWatermark extends Watermark {
	
	public DynamicWatermark(Main parent, float alpha, String name, PImage image, List<SHADERS> shaders) {
		super(parent, name, alpha, false, null, shaders);
		this.image = image;
	}
}
