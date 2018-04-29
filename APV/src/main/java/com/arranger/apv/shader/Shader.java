package com.arranger.apv.shader;

import java.util.List;
import java.util.stream.Collectors;

import com.arranger.apv.Main;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.JSONQuoter;

import ch.bildspur.postfx.builder.PostFXBuilder;

public class Shader extends ShapeSystem {
	
	public static enum SHADERS {
		BINARYGLITCH(b -> b.binaryGlitch(.5f)),
		BLOOM(b -> b.bloom(0.7f, 10, 20)),
		BLUR(b -> b.blur(10, 20, true)),
		BRIGHTNESSCONTRAST(b -> b.brightnessContrast(-0.3f, 1.5f)),
		BRIGHTPASS(b -> b.brightPass(0.6f)),
		CHROMATICABERRATION(b -> b.chromaticAberration()),
		DENOISE(b -> b.denoise(25)),
		EXPOSURE(b -> b.exposure(25)),
		GRAYSCALE(b -> b.grayScale()),
		INVERT(b -> b.invert()),
		NOISE(b -> b.noise(0.1f, 10)),
		PIXELATE(b -> b.pixelate(50)),
		RGBSPLIT(b -> b.rgbSplit(50)),
		SATURATIONVIBRANCE(b -> b.saturationVibrance(-0.5f, 1.0f)),
		SOBEL(b -> b.sobel()),
		TONEMAPPING(b -> b.toneMapping(.5f)),
		TOON(b -> b.toon()),
		VIGNETTE(b -> b.vignette(0.8f, 0.3f));
		
		public ShaderPass shaderPass;
		
		private SHADERS() {
			this((b) -> b.bloom(0.5f, 20, 30));
		}
		
		private SHADERS(ShaderPass shaderPass) {
			this.shaderPass = shaderPass;
		}
	};
	
	@FunctionalInterface
	private static interface ShaderPass {
		void addPass(PostFXBuilder builder);
	}
	
	private List<SHADERS> shaders;
	private String displayName;

	public Shader(Main parent, String displayName, List<SHADERS> shaders) {
		super(parent, null);
		this.shaders = shaders;
		this.displayName = displayName;
		
		//if we don't have a sobel, add one
		if (parent.isAutoAddSobleEnabled()) {
			if (!shaders.contains(SHADERS.SOBEL)) {
				shaders.add(0, SHADERS.SOBEL);
			}
		}
	}
	
	public Shader(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getString(0, Shader.class.getName()), ctx.getShaderList(1));
	}
	
	@Override
	public String getConfig() {
		//{Shader : ["Blur Bloom", [blur, bloom]]}
		JSONQuoter quoter = new JSONQuoter(parent);
		String shaderString = shaders.stream().map(s -> s.name()).collect(Collectors.joining(","));
		return String.format("{%s : [%s, [%s]]}", getName(), quoter.quote(displayName), shaderString);
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void draw() {
		PostFXBuilder render = parent.getPostFX().render();
		shaders.forEach(s -> {
			s.shaderPass.addPass(render);
		});
		
		render.compose();
	}
}
