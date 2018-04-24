package com.arranger.apv.factory;

import java.awt.Shape;

import com.arranger.apv.Main;
import com.arranger.apv.factory.APVShape.Data;
import com.arranger.apv.util.draw.StarMaker;

import processing.core.PShape;

public class StarFactory extends PrimitiveShapeFactory {
	
	private StarMaker maker;
	
	public StarFactory(Main parent) {
		super(parent);
		maker = new StarMaker(parent);
	}

	public StarFactory(Main parent, float scale) {
		super(parent, scale);
		maker = new StarMaker(parent);
	}

	@Override
	public APVShape createShape(Data data) {
		return new PrimitiveShape(parent, data) {
			@Override
			protected Shape createPrimitiveShape(float size) {
				return null; // not used
			}

			@Override
			protected PShape createNewShape() {
				return maker.createStar();
			}
		};
	}
}
