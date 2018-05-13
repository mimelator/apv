package com.arranger.apv.test;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.arranger.apv.util.Configurator;
import com.typesafe.config.ConfigList;

public class AutoSceneTest extends APVPluginTest {

	public AutoSceneTest() {
	}
	
	@Test
	public void testScenes() {
		Configurator cfg = new Configurator(parent);
		ConfigList list = cfg.getRootConfig().getList("setPackList");
		assert(list != null);

		List<String> collect = list.stream().map(e -> (String)e.unwrapped()).collect(Collectors.toList());
		assert(collect != null);
		
		collect.stream().forEach(System.out::println);
		
	}

	@Override
	protected void setFrameIndexes() {

	}

}
