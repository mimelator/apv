package com.arranger.apv.agent;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.arranger.apv.Main;
import com.arranger.apv.shader.Shader.SHADERS;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.DynamicShaderHelper;
import com.arranger.apv.util.FileHelper;

public class AutoLoadWatermarkFolderAgent extends BaseAgent {
	
	private static final Logger logger = Logger.getLogger(AutoLoadWatermarkFolderAgent.class.getName());
	
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
					//do we already have loaded shaders?
					DynamicShaderHelper dynamicShaderHelper = new DynamicShaderHelper(parent);
					if (!dynamicShaderHelper.hasLoadedShaders()) {
						FileHelper fh = new FileHelper(parent);
						String fullPath = fh.getFullPath(autoLoadedFolder);
						Path path = new File(fullPath).toPath().normalize();
						List<Path> backgrounds = fh.getAllFilesFromDir(path, ".jpg");
						dynamicShaderHelper.loadBackgrounds(parent, alpha, shaders, backgrounds, false);
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
}
