package com.arranger.apv.shader;

import com.arranger.apv.Main;
import com.arranger.apv.systems.ShapeSystem;

public class Shader extends ShapeSystem {

	public Shader(Main parent) {
		super(parent, null);
	}

	@Override
	public void draw() {
		parent.getPostFX().render()
		    			.sobel()
		    			.bloom(0.5f, 20, 30)
		    			.compose();
	}

}
