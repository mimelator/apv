package com.arranger.apv.scene;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.back.BackDropSystem;
import com.arranger.apv.color.ColorSystem;
import com.arranger.apv.event.APVChangeEvent.APVChangeEventHandler;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.loc.LocationSystem;
import com.arranger.apv.shader.Shader;
import com.arranger.apv.systems.ShapeSystem;
import com.arranger.apv.util.Configurator;

public class LikedScene extends Scene implements APVChangeEventHandler {

	private Components shadowedComponents;
	boolean needsSetup = false;
	
	public LikedScene(Main parent) {
		super(parent);
	}

	public LikedScene(Configurator.Context ctx) {
		this(ctx.getParent());
		
		if (ctx.argList.size() == 0) {
			return;
		}
		
		setSystems((BackDropSystem)ctx.loadPlugin(0),
				(ShapeSystem)ctx.loadPlugin(1),
				(ShapeSystem)ctx.loadPlugin(2),
				(Filter)ctx.loadPlugin(3),
				(Shader)ctx.loadPlugin(4),
				(ColorSystem)ctx.loadPlugin(5),
				(LocationSystem)ctx.loadPlugin(6));
		needsSetup = true;
	}

	public LikedScene(Scene o) {
		super(o);
	}

	@Override
	public void drawScene() {
		if (needsSetup) {
			setup();
		}
		super.drawScene();
	}
	
	@Override
	public void onPluginChange(APV<? extends APVPlugin> apv, APVPlugin plugin, String cause) {
		if (shadowedComponents == null) {
			shadowedComponents = new Components(cc); //make a copy
		}
		
		switch (apv.getSystemName()) {
		case BACKGROUNDS:
			shadowedComponents.bgSys = (ShapeSystem)plugin;
			break;
		case BACKDROPS:
			shadowedComponents.backDrop = (BackDropSystem)plugin;
			break;
		case COLORS:
			shadowedComponents.colorSys = (ColorSystem)plugin;
			break;
		case FILTERS:
			shadowedComponents.filter = (Filter)plugin;
			break;
		case SHADERS:
			shadowedComponents.shader = (Shader)plugin;
			break;
		case FOREGROUNDS:
			shadowedComponents.fgSys = (ShapeSystem)plugin;
			break;
		case LOCATIONS:
			shadowedComponents.locSys = (LocationSystem)plugin;
			break;
		case LIKED_SCENES:
			shadowedComponents = null; //happens every time this is 'relaunched'
		default:
			//do nothing
		}
	}
	
	@Override
	public Components getComponentsToDrawScene() {
		return shadowedComponents != null ? shadowedComponents : cc;
	}

	@Override
	public void setup() {
		setup(cc.backDrop);
		setup(cc.bgSys);
		setup(cc.fgSys);
		setup(cc.filter);
		setup(cc.shader);
		setup(cc.colorSys);
		setup(cc.locSys);
		needsSetup = false;
	}
	
	private void setup(APVPlugin ss) {
		if (ss != null) {
			ss.setup();
		}
	}
}
