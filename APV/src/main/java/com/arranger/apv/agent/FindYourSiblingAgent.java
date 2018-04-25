package com.arranger.apv.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.Main.SYSTEM_NAMES;
import com.arranger.apv.scene.Scene.Components;
import com.arranger.apv.util.Configurator;

public class FindYourSiblingAgent extends PulseAgent {

	private static final Logger logger = Logger.getLogger(FindYourSiblingAgent.class.getName());
	
	public FindYourSiblingAgent(Main parent, int numPulses) {
		super(parent, numPulses);
	}
	
	public FindYourSiblingAgent(Configurator.Context ctx) {
		super(ctx);
	}

	@Override
	protected void onPulse() {
		
		APV<? extends APVPlugin> apvSystem = null;
		APVPlugin plugin = null;
		while (plugin == null) {
			//pick a random full system
			SYSTEM_NAMES system = getNextCandidateSystem();
			logger.fine("Finding siblings for system: " + system.name);
			
			apvSystem = parent.getSystem(system);
			Components comps = parent.getCurrentScene().getComponentsToDrawScene();
			plugin = comps.getComponentFromSystem(system);
		}
		
		logger.fine("found current plugin: " + plugin.getDisplayName());
		List<APVPlugin> siblings = getSiblings(plugin, apvSystem);
		
		logger.fine("found siblings: " + siblings);
		APVPlugin result =  siblings.get((int)parent.random(siblings.size()));
		
		logger.fine("setting next plugin: " + result);
		apvSystem.setNextPlugin(result, getName());
	}

	protected SYSTEM_NAMES getNextCandidateSystem() {
		SYSTEM_NAMES system = null;
		while (system == null) {
			system = Main.SYSTEM_NAMES.random();
			if (!isCandidateSystem(system)) {
				system = null;
			}
		}
		return system;
	}
	
	protected boolean isCandidateSystem(SYSTEM_NAMES system) {
		switch (system) {
			case BACKDROPS:
			case BACKGROUNDS:
			case FOREGROUNDS:
			case FILTERS:
			case COLORS:
			case LOCATIONS:
				return true;
			default:
				return false;
		}
	}
	
	protected List<APVPlugin> getSiblings(APVPlugin plugin, APV<? extends APVPlugin> apvSystem) {
		List<APVPlugin> results = new ArrayList<APVPlugin>();
		
		Class<? extends APVPlugin> pluginClass = plugin.getClass();
		apvSystem.forEach(p -> {
			if (pluginClass.equals(p.getClass())) {
				results.add(p);
			}
		});
		
		return results;
	}
}
