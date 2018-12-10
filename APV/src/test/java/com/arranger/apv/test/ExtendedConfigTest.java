package com.arranger.apv.test;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.systems.lite.FreqDetector;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ExtendedConfigTest extends ConfigBasedTest {

	private static final String CONFIG = "{FreqDetector : []}";
	private static final String EXTENDED = "{enabled: false, popularityIndex: 20}";
	
	public ExtendedConfigTest() {
	}

	
	@Test
	public void testExtendedObjLoading() {
		APVPlugin obj = cfg.loadObjectFromConfig(CONFIG);
		assert(obj != null);
		assert(obj instanceof FreqDetector);
		
		Config extendedConf = ConfigFactory.parseReader(new StringReader(EXTENDED));  
		assert(extendedConf != null);
		obj.setEnabled(extendedConf.getBoolean("enabled"));
		obj.setPopularityIndex(extendedConf.getInt("popularityIndex"));
		
		assert(!obj.isEnabled());
		assert(obj.getPopularityIndex() == 20);
	}
	
	@Test
	public void testIdentifyingExtendedAttributes() {
		Config config = ConfigFactory.parseReader(new StringReader(CONFIG));  
		assert(config != null);
		boolean anyMatch = config.entrySet().stream().anyMatch(entry -> entry.getKey().equals("enabled"));
		assert(!anyMatch);
		
		Config extendedConf = ConfigFactory.parseReader(new StringReader(EXTENDED));  
		assert(extendedConf != null);
		boolean anyMatch2 = extendedConf.entrySet().stream().anyMatch(entry -> entry.getKey().equals("enabled"));
		assert(anyMatch2);
	}
}
