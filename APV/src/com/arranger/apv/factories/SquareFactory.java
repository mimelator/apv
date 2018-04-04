package com.arranger.apv.factories;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.arranger.apv.APVShape;
import com.arranger.apv.APVShape.Data;
import com.arranger.apv.Main;

public class SquareFactory  extends PrimitiveShapeFactory {

	public SquareFactory(Main parent) {
		super(parent);
	}
	
	public SquareFactory(Main parent, float scale) {
		super(parent, scale);
	}

	@Override
	public APVShape createShape(Data data) {
		return new PrimitiveShape(parent, data) {
			@Override
			protected Shape createPrimitiveShape(float size) {
				return new Rectangle2D.Float(0, 0, size, size);
			}
		};
	}
}

