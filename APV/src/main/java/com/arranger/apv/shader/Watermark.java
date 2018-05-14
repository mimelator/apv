package com.arranger.apv.shader;

import java.util.List;
import java.util.stream.Collectors;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.JSONQuoter;

import ch.bildspur.postfx.Supervisor;
import ch.bildspur.postfx.pass.BasePass;
import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PShader;

public class Watermark extends CustomShader {
	
	private static final float ROT_HIGH = PI / 8f;
	private static final float ROT_LOW = -ROT_HIGH;
	
	private static final String WATERMARK = "watermark";
	private boolean resize = false;
	private boolean rotate = false;

	public Watermark(Main parent, String displayName, float alpha, boolean resize, boolean rotate, String imageFile, List<SHADERS> shaders) {
		super(parent, WATERMARK, alpha, imageFile, shaders);
		this.displayName = displayName;
		this.resize = resize;
		this.rotate = rotate;
	}

	public Watermark(Configurator.Context ctx) {
		this(ctx.getParent(), 
				ctx.getString(0, WATERMARK),
				ctx.getFloat(1, ALPHA),
				ctx.getBoolean(2, false),
				ctx.getBoolean(3, false), 
				ctx.getString(4, null),
				ctx.getShaderList(5));
	}

	public String getConfig() {
		//{Watermark : [logo, .5, false, false, "texture/wl2.jpg", [blur]]}
		JSONQuoter quoter = new JSONQuoter(parent);
		String shaderString = shaders.stream().map(s -> s.name()).collect(Collectors.joining(","));
		return String.format("{%s : [%s, %s, %b, %b, %s, [%s]]}", 
				getName(), 
				quoter.quote(displayName), 
				alpha,
				resize,
				rotate,
				imageFile,
				shaderString);
	}
	
	@Override
	protected void checkImageSize(PImage image) {
		if (resize) {
			if (rotate) {
				image.resize(parent.width * 2, parent.height * 2);
			} else {
				image.resize(parent.width, parent.height);
			}
		}
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
			
			shader.set("rotate", rotate);
			
			if (rotate) {
				float angle = parent.oscillate(ROT_LOW, ROT_HIGH, 100);
				shader.set("angle", angle);
			}
		}
	}
}
