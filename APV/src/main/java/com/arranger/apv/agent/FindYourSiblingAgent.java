package com.arranger.apv.agent;

import java.util.List;
import java.util.logging.Logger;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.Main.SYSTEM_NAMES;
import com.arranger.apv.scene.Scene.Components;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.RandomHelper;

public class FindYourSiblingAgent extends PulseAgent {

	
	private static final Logger logger = Logger.getLogger(FindYourSiblingAgent.class.getName());
	private static final int MAX_ATTEMPTS = 10;
	
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
		int attempt = 0;
		while (plugin == null && attempt++ < MAX_ATTEMPTS) {
			SYSTEM_NAMES system = getNextCandidateSystem();
			logger.fine("Finding siblings for system: " + system.name);
			
			apvSystem = parent.getSystem(system);
			Components comps = parent.getCurrentScene().getComponentsToDrawScene();
			plugin = comps.getComponentFromSystem(system);
		}
		
		//Couldn't find one after MAX_ATTEMPTS
		if (plugin == null) {
			return;
		}
		
		logger.fine("found current plugin: " + plugin.getDisplayName());
		List<? extends APVPlugin> siblings = apvSystem.getSiblings(plugin);
		
		logger.fine("found siblings: " + siblings);
		APVPlugin result = new RandomHelper(parent).random(siblings);
		
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
	
	
}
