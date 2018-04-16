package com.arranger.apv.test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.arranger.apv.APVPlugin;

public class AnnotationTester extends APVPluginTest {

	private static final String APV_PACKAGE = "com.arranger.apv";
	private static final String APV_PACKAGE_EXCLUDE_UTIL = "com.arranger.apv.util";
	private static final String APV_PACKAGE_EXCLUDE_ARCHIVE = "com.arranger.apv.archive";
	private static final String APV_PACKAGE_EXCLUDE_AUDIO = "com.arranger.apv.audio";


	public AnnotationTester() {
	}


	@Test
	public void testInitialScan() throws Exception {
		
		Reflections reflections = new Reflections(
					new ConfigurationBuilder().
						filterInputsBy(new FilterBuilder().
								includePackage(APV_PACKAGE).
								excludePackage(APV_PACKAGE_EXCLUDE_UTIL).
								excludePackage(APV_PACKAGE_EXCLUDE_AUDIO).
								excludePackage(APV_PACKAGE_EXCLUDE_ARCHIVE)).
						setUrls(ClasspathHelper.forPackage(APV_PACKAGE)));
		
		
		Set<Class<? extends APVPlugin>> target = reflections.getSubTypesOf(APVPlugin.class);
		
		List<String> results = target.stream()
				.map(clz -> clz.getName())
				.sorted()
				.collect(Collectors.toList());
		
		results.stream().forEach(System.out::println);
	}
	
	
	@Override
	protected void setFrameIndexes() {

	}
	
}
