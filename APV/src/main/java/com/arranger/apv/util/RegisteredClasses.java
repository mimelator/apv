package com.arranger.apv.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

public class RegisteredClasses extends APVPlugin {
	
	private static final String APV_PACKAGE = "com.arranger.apv";
	private static final String APV_PACKAGE_EXCLUDE_UTIL = "com.arranger.apv.util";
	private static final String APV_PACKAGE_EXCLUDE_ARCHIVE = "com.arranger.apv.archive";
	private static final String APV_PACKAGE_EXCLUDE_AUDIO = "com.arranger.apv.audio";

	private Reflections reflections;
	private Map<String, Class<?>> classMap; 
	
	public RegisteredClasses(Main parent) {
		super(parent);
	}

	public Map<String, Class<?>> getClassMap() {
		if (reflections == null) {
			initialize();
		}
		
		return classMap;
	}
	
	private void initialize() {
		reflections = new Reflections(
				new ConfigurationBuilder().
					filterInputsBy(new FilterBuilder().
							include(FilterBuilder.prefix(APV_PACKAGE)).
							excludePackage(APV_PACKAGE_EXCLUDE_UTIL).
							excludePackage(APV_PACKAGE_EXCLUDE_AUDIO).
							excludePackage(APV_PACKAGE_EXCLUDE_ARCHIVE)).
					setUrls(ClasspathHelper.forPackage(APV_PACKAGE)));
		
		Set<Class<? extends APVPlugin>> target = reflections.getSubTypesOf(APVPlugin.class);
		classMap = new HashMap<String, Class<?>>();
		target.stream().forEach(cls -> classMap.put(cls.getSimpleName(), cls));
	}
}
