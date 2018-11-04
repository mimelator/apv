package com.arranger.apv.agent;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.arranger.apv.Main;
import com.arranger.apv.shader.Shader.SHADERS;
import com.arranger.apv.shader.Watermark;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.RandomHelper;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * NB: Don't use TOON from shaders as in this situation it hides all foreground activity
 */
public class AutoLoadWatermarkFolderAgent extends BaseAgent {
	
	private static final Logger logger = Logger.getLogger(AutoLoadWatermarkFolderAgent.class.getName());
	
	@SuppressWarnings("unchecked")
	private static List<SHADERS> bannedShaders = Arrays.asList(new SHADERS[] {SHADERS.BLOOM, SHADERS.TOON});
	
	private List<SHADERS> shaders;
	private float alpha;
	private boolean hasLoaded = false;
	
	public AutoLoadWatermarkFolderAgent(Main parent, float alpha, List<SHADERS> shaders) {
		super(parent);
		this.alpha = alpha;
		this.shaders = shaders;
		
		registerAgent(getDrawEvent(), ()->{
			if (hasLoaded) {
				return;
			}
			
			hasLoaded = true;
			try {
				String autoLoadedFolder = parent.getConfigValueForFlag(Main.FLAGS.AUTO_LOADED_BACKGROUND_FOLDER);
				if (autoLoadedFolder != null) {
					FileHelper fh = new FileHelper(parent);
					
					String fullPath = fh.getFullPath(autoLoadedFolder);
					Path path = new File(fullPath).toPath().normalize();
					List<Path> backgrounds = fh.getAllFilesFromDir(path, ".jpg");
					
					RandomHelper rh = new RandomHelper(parent);
					
					for (Path p : backgrounds) {
						SHADERS random = rh.random(shaders);
						while (!isShaderOk(random)) {	//see note
							random = rh.random(shaders);
						}
						@SuppressWarnings("unchecked")
						List<SHADERS> randShader = Arrays.asList(new SHADERS[] {random});
						
						Watermark watermark = new Watermark(parent, p.getFileName().toString(), alpha, false, p.toString(), randShader);
						parent.getShaders().addPlugin(watermark);
						System.out.println("Loading watermark shader: " + watermark.getDisplayName() + " with shader: " + random.name());
					}
				}
			} catch (Exception e) {
				logger.log(Level.INFO, e.getMessage(), e);
			}
		});
	}
	
	public AutoLoadWatermarkFolderAgent(Configurator.Context ctx) {
		this(ctx.getParent(), 
				ctx.getFloat(0, ALPHA),
				ctx.getShaderList(1));
	}

	@Override
	public String getConfig() {
		//{AutoLoadWatermarkFolderAgent : [.3, []]}
		String shaderString = shaders.stream().map(s -> s.name()).collect(Collectors.joining(","));
		return String.format("{%s : [%s, [%s]]}", 
				getName(), 
				alpha,
				shaderString);
	}
	
	private boolean isShaderOk(SHADERS s) {
		return !bannedShaders.contains(s);
	}
}
