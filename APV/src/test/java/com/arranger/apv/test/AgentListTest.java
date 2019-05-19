package com.arranger.apv.test;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.agent.BaseAgent;
import com.arranger.apv.util.Configurator;

public class AgentListTest extends ConfigBasedTest {

	public AgentListTest() {
	}
	
	@BeforeEach
	public void beforeEach() {
		super.beforeEach();
	}

	@Test
	public void testListingAgents() {
		Configurator cfg = new Configurator(parent);
		
		List<? extends APVPlugin> agentPlugins = cfg.loadAVPPlugins(Main.SYSTEM_NAMES.AGENTS);
		assert(agentPlugins != null);
		
		for (APVPlugin plugin : agentPlugins) {
			assert(plugin instanceof BaseAgent);
			BaseAgent agent = (BaseAgent)plugin;
			System.out.println(agent.getConfig());
		}
	}
	
	
}
