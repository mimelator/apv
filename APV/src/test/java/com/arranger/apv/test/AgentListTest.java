package com.arranger.apv.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.arranger.apv.util.Configurator;
import com.typesafe.config.ConfigList;

public class AgentListTest extends ConfigBasedTest {

	public AgentListTest() {
	}
	
	@BeforeEach
	public void beforeEach() {
		super.beforeEach();
		//add a mock for isDisabled in Configurator to always return true
	}

	@Test
	public void testListingAgents() {
		Configurator cfg = new Configurator(parent);
		ConfigList list = cfg.getRootConfig().getList("setPackList");
		assert(list != null);
		
//		List<? extends APVPlugin> agentPlugins = cfg.loadAVPPlugins(Main.SYSTEM_NAMES.AGENTS);
//		assert(agentPlugins != null);
	}
	
	
}
