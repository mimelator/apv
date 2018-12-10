package com.arranger.apv.util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.shader.Shader;
import com.arranger.apv.shader.Shader.SHADERS;
import com.arranger.apv.shader.Watermark;

import edu.emory.mathcs.backport.java.util.Arrays;

@SuppressWarnings("unchecked")
public class DynamicShaderHelper extends APVPlugin {
	
	private static final String JPG = ".jpg";
	private static List<SHADERS> bannedShaders = Arrays.asList(new SHADERS[] {SHADERS.BLOOM, SHADERS.TOON});

	public DynamicShaderHelper(Main parent) {
		super(parent);
	}

	public List<Watermark> loadBackgrounds(Main parent, float alpha, List<SHADERS> shaders, List<Path> backgrounds, boolean removePrevious) {
		APV<Shader> shaderSystem = parent.getShaders();
		
		if (removePrevious) {
			for (Iterator<Shader> it = shaderSystem.getList().iterator(); it.hasNext();) {
				Shader s = it.next();
				if (s.getDisplayName().endsWith(JPG)) {
					it.remove();
				}
			}
		}
		
		List<Watermark> results = new ArrayList<Watermark>();
		
		RandomHelper rh = new RandomHelper(parent);
		for (Path p : backgrounds) {
			SHADERS random = rh.random(shaders);
			while (!isShaderOk(random)) {	//see note
				random = rh.random(shaders);
			}
			
			List<SHADERS> randShader = Arrays.asList(new SHADERS[] {random});
			Watermark watermark = new Watermark(parent, p.getFileName().toString(), alpha, false, p.toString(), randShader);
			shaderSystem.addPlugin(watermark);
			results.add(watermark);
			System.out.println("Loading watermark shader: " + watermark.getDisplayName() + " with shader: " + random.name());
		}
		
		return results;
	}
	
	public boolean hasLoadedShaders() {
		APV<Shader> shaderSystem = parent.getShaders();
		boolean hasLoadedShaders = false;
		
		for (Iterator<Shader> it = shaderSystem.getList().iterator(); it.hasNext();) {
			if (it.next().getDisplayName().endsWith(JPG)) {
				hasLoadedShaders = true;
				break;
			}
		}
		return hasLoadedShaders;
	}
	
	private boolean isShaderOk(SHADERS s) {
		return !bannedShaders.contains(s);
	}
}
