package com.arranger.apv.archive;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import com.arranger.apv.factory.APVShape;
import com.arranger.apv.factory.PrimitiveShapeFactory;
import com.arranger.apv.factory.APVShape.Data;
import com.arranger.apv.Main;

/**
 * To be deleted
 */
public class CircleFactory extends PrimitiveShapeFactory {

	private CircleFactory(Main parent) {
		super(parent);
	}
	
	private CircleFactory(Main parent, float scale) {
		super(parent, scale);
	}

	@Override
	public APVShape createShape(Data data) {
		return new PrimitiveShape(parent, data) {
			@Override
			protected Shape createPrimitiveShape(float size) {
				return new Ellipse2D.Float(0, 0, size, size);
			}
		};
	}
}
