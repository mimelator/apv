package com.arranger.apv.shader;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

import ch.bildspur.postfx.Supervisor;
import ch.bildspur.postfx.builder.PostFXBuilder;
import ch.bildspur.postfx.pass.BasePass;
import processing.core.PApplet;
import processing.opengl.PShader;

public class CustomShader extends Shader {
	
	private static final float ALPHA = .5f;
	
	private CustomShaderPass pass;

	public CustomShader(Main parent, String shaderName) {
		super(parent, shaderName, null);
		pass = new CustomShaderPass(parent);
	}
	
	public CustomShader(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getString(0, null));
	}
	
	@Override
	public String getConfig() {
		//{CustomShader : [monjori]}
		return String.format("{%s : [%s]}", getName(), getDisplayName());
	}

	@Override
	public void draw() {
		PostFXBuilder render = parent.getPostFX().render();
		render.custom(pass);
		render.compose();
	}
	
	private class CustomShaderPass extends BasePass {

		public CustomShaderPass(PApplet sketch) {
			super(sketch, getDisplayName());
		}
		
	    @Override
	    public void prepare(Supervisor supervisor) {
	        PShader shader = getShader();
	        shader.set("resolution", (float)parent.width, (float)parent.height);
			shader.set("time", parent.millis() / 1000.0f);
			shader.set("alpha", ALPHA);
	    }
	}
}
