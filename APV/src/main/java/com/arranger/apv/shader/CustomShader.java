package com.arranger.apv.shader;

import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

import ch.bildspur.postfx.Supervisor;
import ch.bildspur.postfx.builder.PostFXBuilder;
import ch.bildspur.postfx.pass.BasePass;
import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PShader;

public class CustomShader extends Shader {

	private static final String DEFAULT_TEXTURE_NAME = "myTexture";
	protected static final float ALPHA = .5f;
	
	protected BasePass pass;
	protected float alpha;
	protected PImage image;
	protected String imageFile;
	protected String textureName = DEFAULT_TEXTURE_NAME;

	public CustomShader(Main parent, String shaderName, float alpha, String imageFile, List<SHADERS> shaders) {
		super(parent, shaderName, shaders);
		parent.textureWrap(REPEAT);
		pass = createCustomPass(parent);
		this.alpha = alpha;
		if (imageFile != null) {
			image = parent.loadImage(imageFile);
			this.imageFile = imageFile;
		}
	}

	protected BasePass createCustomPass(Main parent) {
		return new CustomShaderPass(parent);
	}
	
	public CustomShader(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getString(0, null), ctx.getFloat(1, ALPHA), ctx.getString(2, null), null);
	}
	
	@Override
	public String getConfig() {
		//{CustomShader : [monjori]}
		if (imageFile == null) {
			return String.format("{%s : [%s, %s]}", getName(), getDisplayName(), alpha);
		} else {
			return String.format("{%s : [%s, %s, %s]}", getName(), getDisplayName(), alpha, imageFile);	
		}
		
	}

	@Override
	public void draw() {
		PostFXBuilder render;
		try {
			render = parent.getPostFX().render();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		if (shaders != null) {
			shaders.forEach(s -> {
				s.getShaderPass().addPass(render);
			});
		}
		
		try {
			render.custom(pass);
			render.compose();
		} catch (Exception e) {
			System.out.println("Exception caught for custom shader: " + getDisplayName());
			e.printStackTrace();
		}
	}
	
	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	public PImage getImage() {
		return image;
	}

	public void setImage(PImage image) {
		this.image = image;
	}

	protected class CustomShaderPass extends BasePass {

		protected CustomShaderPass(PApplet sketch) {
			super(sketch, getDisplayName());
		}
		
	    @Override
	    public void prepare(Supervisor supervisor) {
	        PShader shader = getShader();
	        shader.set("resolution", (float)parent.width, (float)parent.height);
			shader.set("time", parent.millis() / 1000.0f);
			shader.set("alpha", getAlphaForShaderPass());
			
			if (image != null) {
				shader.set(textureName, image);
				shader.set("textureResolution", image.width, image.height);
			}
	    }

		protected float getAlphaForShaderPass() {
			return alpha;
		}
	}
}
