package com.arranger.apv.test;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.ShapeSystem;
import com.arranger.apv.util.Configurator;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;

public class ConfiguratorTest extends APVPluginTest {
	
	private static final String CONFIG = "alt.properties";
	
	
	@Test
	public void testClassResources() throws Exception {
		testClassResource("reference.conf");
		testClassResource("application.conf");
	}
	
	
	Configurator cfg;
	
	@Test
	public void testLoadOtherConfig() throws Exception {
		
		getInputStream(CONFIG, inputStream -> {
			loadConfigurator(inputStream);
		});
		
		assert(cfg != null);
		Config rootConfig = cfg.getRootConfig();
		assert(rootConfig != null);
		debug(rootConfig.toString());
	}
	
	protected void loadConfigurator(InputStream is) throws Exception {
		cfg = new Configurator(parent, is);
	}
	
	
	@Test
	public void testClassLoading() throws Exception {
		Configurator cfg = new Configurator(parent);
		
		List<? extends Config> scl = cfg.getSystemConfigList("backgroundSystems");
		for (Iterator<? extends Config> it = scl.iterator(); it.hasNext();) {
			Config wrapperObj = it.next();
			Entry<String, ConfigValue> shapeConfigObj = wrapperObj.entrySet().iterator().next();
			
			APVPlugin plugin = cfg.loadPlugin(shapeConfigObj.getKey(), (ConfigList)shapeConfigObj.getValue());
			assert(plugin != null);
			assert(plugin instanceof ShapeSystem);
		}
		
		List<? extends APVPlugin> ss = cfg.loadAVPPlugins("backgroundSystems");
		assert(ss != null);
		assert(ss.size() > 3);
		
		ss = cfg.loadAVPPlugins("colorSystems");
		assert(ss != null);
		assert(ss.size() > 2);
		
		ss = cfg.loadAVPPlugins("controlSystems");
		assert(ss != null);
		assert(ss.size() > 3);
		
		ss = cfg.loadAVPPlugins("locationSystems");
		assert(ss != null);
		assert(ss.size() > 4);
	}
	
	public void testClassResource(String resource) throws Exception {
		Enumeration<URL> resources = ConfiguratorTest.class.getClassLoader().getResources(resource);
		while (resources.hasMoreElements()) {
			URL nextElement = resources.nextElement();
			debug(nextElement.toString());
		}
	}
	
	
	@Override
	protected void setFrameIndexes() {
		
	}

}
