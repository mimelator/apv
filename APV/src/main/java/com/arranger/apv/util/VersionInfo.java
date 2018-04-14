package com.arranger.apv.util;

import java.net.URL;
import java.util.Properties;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class VersionInfo extends APVPlugin {

	private String version;

	public VersionInfo(Main parent) {
		super(parent);
		
		try {
			URL resource = getClass().getClassLoader().getResource("META-INF/maven/APV/APV/pom.properties");
			Properties props = new Properties();
			props.load(resource.openStream());
			version = props.getProperty("version");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getVersion() {
		return version;
	}
}
