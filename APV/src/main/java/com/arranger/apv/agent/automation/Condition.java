package com.arranger.apv.agent.automation;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.scene.Scene.Components;
import com.arranger.apv.util.Configurator;

public class Condition extends APVPlugin {

	private boolean target;
	private Main.SYSTEM_NAMES system;
	private String pluginName;
	
	public Condition(Main parent, boolean target) {
		this(parent, target, null, null);
	}
	
	public Condition(Main parent, boolean target, Main.SYSTEM_NAMES system, String pluginName) {
		super(parent);
		this.target = target;
		this.system = system;
		this.pluginName = pluginName;
	}

	public Condition(Configurator.Context ctx) {
		this(ctx.getParent(),
				ctx.getBoolean(0, true),
				ctx.getSystemName(1, null),
				ctx.getString(2, null));
		
	}
	
	@Override
	public String getConfig() {
		//{Condition : [false, FOREGROUNDS, Carnvial]},
		if (system != null && pluginName != null) {
			return String.format("{%s : [%b, %s, %s]}", 
								getName(),
								target,
								system,
								pluginName);
		} else {
			return String.format("{%s : [%b]}", getName(), target);
		}
	}
	
	public boolean isTrue() {
		if (system != null && pluginName != null) {
			Components comps = parent.getCurrentScene().getComponentsToDrawScene();
			APVPlugin comp = comps.getComponentFromSystem(system);
			if (comp != null) {
				return target == comp.getName().equals(pluginName);
			}
		}
		return target;
	}

}
