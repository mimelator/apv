package com.arranger.apv.factories;

import com.arranger.apv.APVShape;
import com.arranger.apv.Main;
import com.arranger.apv.ShapeFactory;
import com.arranger.apv.APVShape.Data;

public class EmptyShapeFactory extends ShapeFactory {
	
	public EmptyShapeFactory(Main parent) {
		super(parent);
	}
	public APVShape createShape(Data data) {
		return null;
	}
}