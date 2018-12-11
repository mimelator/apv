package com.arranger.apv.shader;

import java.util.List;
import java.util.stream.Collectors;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.JSONQuoter;

import ch.bildspur.postfx.Supervisor;
import ch.bildspur.postfx.pass.BasePass;
import processing.core.PApplet;
import processing.opengl.PShader;

public class Watermark extends CustomShader {
	
	private static final String WATERMARK = "watermark";
	private boolean scroll = false;

	public Watermark(Main parent, String displayName, float alpha, boolean scroll, String imageFile, List<SHADERS> shaders) {
		super(parent, WATERMARK, alpha, imageFile, shaders);
		this.displayName = displayName;
		this.scroll = scroll;
	}

	public Watermark(Configurator.Context ctx) {
		this(ctx.getParent(), 
				ctx.getString(0, WATERMARK),
				ctx.getFloat(1, ALPHA),
				ctx.getBoolean(2, false),
				ctx.getString(3, null),
				ctx.getShaderList(4));
	}

	public String getConfig() {
		//{Watermark : [logo, .5, "texture/wl2.jpg", [blur]]}
		JSONQuoter quoter = new JSONQuoter(parent);
		String shaderString;
		if (shaders == null) {
			//I'm not sure how this can happen
			System.out.println("Null value for shaders in Watermark");
			shaderString = "[]";
		} else {
			shaderString = shaders.stream().map(s -> s.name()).collect(Collectors.joining(","));
		}
		
		return String.format("{%s : [%s, %s,%b,  %s, [%s]]}", 
				getName(), 
				quoter.quote(displayName), 
				alpha,
				scroll,
				quoter.quote(imageFile),
				shaderString);
	}
	
	@Override
	protected BasePass createCustomPass(Main parent) {
		return new WatermarkShaderPass(parent);
	}

	protected class WatermarkShaderPass extends CustomShaderPass {

		public WatermarkShaderPass(PApplet sketch) {
			super(sketch);
		}

		@Override
		public void prepare(Supervisor supervisor) {
			super.prepare(supervisor);
			PShader shader = getShader();
			shader.set("scroll", scroll);
		}
		
		@Override
		protected float getAlphaForShaderPass() {
			return alpha;
		}
	}
}
