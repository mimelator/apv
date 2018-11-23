package com.arranger.apv.shader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.arranger.apv.Main;
import com.arranger.apv.Main.FLAGS;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FFTAnalysis;
import com.arranger.apv.util.JSONQuoter;

import ch.bildspur.postfx.builder.PostFXBuilder;

public class Shader extends ShapeSystem {
	
	private static float LOW = .75f;
	private static float HIGH = 1.25f;
	
	public static enum SHADERS {
		BINARYGLITCH((b, p) -> {
			float def = .5f;
			float res = map(p, def);
			
			b.binaryGlitch(res);
		}),
		BLOOM((b, p) -> {
			float def = .7f;
			float res = map(p, def);
			
			b.bloom(res, 10, 20);
		}),
		BLUR((b, p) -> {
			int def = 10;
			int res = (int)map(p, def);
			
			b.blur(res, 20, true);
		}),
		BRIGHTNESSCONTRAST((b, p) -> {
			float def = 3.5f;
			float res = map(p, def);
			
			b.brightnessContrast(-.2f, res);
		}),
		BRIGHTPASS((b, p) -> {
			float def = .7f;
			float res = map(p, def);
			
			b.brightPass(res);
		}),
		CHROMATICABERRATION((b, p) -> {
			b.chromaticAberration();
		}),
		DENOISE((b, p) -> {
			int def = -5;
			int res = (int)map(p, def);
			
			b.denoise(res);
		}), 
		EXPOSURE((b, p) -> {
			int def = 25;
			int res = (int)map(p, def);
			
			b.exposure(res);
		}),
		GRAYSCALE((b, p) -> {
			b.grayScale();
		}),
		INVERT((b, p) -> {
			b.invert();
		}),
		NOISE((b, p) -> {
			float def = .1f;
			float res = map(p, def);
			
			b.noise(res, 10);
		}),
		PIXELATE((b, p) -> {
			int def = 50;
			int res = (int)map(p, def);
			
			b.pixelate(res);
		}), 
		PIXELATE200((b, p) -> {
			int def = 200;
			int res = (int)map(p, def);
			
			b.pixelate(res);
		}), 
		RGBSPLIT((b, p) -> {
			int def = 50;
			int res = (int)map(p, def);
			
			b.rgbSplit(res);
		}),
		SATURATIONVIBRANCE((b, p) -> {
			float def = -.5f;
			float res = map(p, def);
			
			b.saturationVibrance(res, 1.0f);
		}),
		SOBEL((b, p) -> {
			b.sobel();
		}),
		TONEMAPPING((b, p) -> {
			float def = .5f;
			float res = map(p, def);
			
			b.toneMapping(res);
		}),
		TOON((b, p) -> {
			b.toon();
		}),
		VIGNETTE((b, p) -> {
			float def = .8f;
			float res = map(p, def);
			
			b.vignette(res, 0.3f);
		});
		
		private ShaderPass shaderPass;
		
		private SHADERS() {
			this((b, p) -> {
				b.bloom(0.5f, 20, 30);
			});
		}
		
		private SHADERS(ShaderPass shaderPass) {
			this.shaderPass = shaderPass;
		}
		
		public ShaderPass getShaderPass() {
			return shaderPass;
		}
		
		public static final List<SHADERS> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
	};
	
	private static FFTAnalysis fftAnalysis;
	
	private static float map(Main parent, float defaultValue) {
		if (fftAnalysis == null) {
			fftAnalysis = new FFTAnalysis(parent);
		}
		return fftAnalysis.getMappedAmp(0, 1, LOW * defaultValue, HIGH * defaultValue);
	}
	
	@FunctionalInterface
	protected static interface ShaderPass {
		void addPass(PostFXBuilder builder, Main parent);
	}
	
	protected List<SHADERS> shaders;
	protected String displayName;

	public Shader(Main parent, String displayName, List<SHADERS> shaders) {
		super(parent, null);
		this.shaders = shaders;
		this.displayName = displayName;
		
		//if we don't have a sobel, add one
		if (parent.isAutoAddSobleEnabled() && shaders != null) {
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
		if (shaders.isEmpty()) {
			return;
		}
		
		PostFXBuilder render = parent.getPostFX().render();
		shaders.forEach(s -> {
			s.shaderPass.addPass(render, parent);
		});
		
		render.compose();
	}

	@Override
	public void onFactoryUpdate() {
	}
}
