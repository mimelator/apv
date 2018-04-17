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
	Configurator configurator;
	
	@Test
	public void testConfigOverride() throws Exception {
		Configurator cfg = new Configurator(parent);
		
		String string = cfg.getRootConfig().getString("apv.fullScreen");
		debug("apv.fullScreen: " + string);
		debug("apv.fullScreen [sysProp]: " + System.getProperty("apv.fullScreen"));
	}
	
	
	@Test
	public void testClassResources() throws Exception {
		testClassResource("reference.conf");
		testClassResource("application.conf");
	}
	
	@Test
	public void testLoadOtherConfig() throws Exception {
		
		getInputStream(CONFIG, inputStream -> {
			loadConfigurator(inputStream);
		});
		
		assert(configurator != null);
		Config rootConfig = configurator.getRootConfig();
		assert(rootConfig != null);
		debug(rootConfig.toString());
	}
	
	protected void loadConfigurator(InputStream is) throws Exception {
		configurator = new Configurator(parent, is);
	}
	
	@Test
	public void testClassLoading() throws Exception {
		Configurator cfg = new Configurator(parent);
		
		List<? extends Config> scl = cfg.getSystemConfigList("backgrounds");
		for (Iterator<? extends Config> it = scl.iterator(); it.hasNext();) {
			Config wrapperObj = it.next();
			Entry<String, ConfigValue> shapeConfigObj = wrapperObj.entrySet().iterator().next();
			
			APVPlugin plugin = cfg.loadPlugin(shapeConfigObj.getKey(), (ConfigList)shapeConfigObj.getValue());
			assert(plugin != null);
			assert(plugin instanceof ShapeSystem);
		}
		
		List<? extends APVPlugin> ss = cfg.loadAVPPlugins("backgrounds");
		assert(ss != null);
		assert(ss.size() > 3);
		
		ss = cfg.loadAVPPlugins("colors");
		assert(ss != null);
		assert(ss.size() > 2);
		
		ss = cfg.loadAVPPlugins("controls");
		assert(ss != null);
		assert(ss.size() > 3);
		
		ss = cfg.loadAVPPlugins("locations");
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
