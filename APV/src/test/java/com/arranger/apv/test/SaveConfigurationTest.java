package com.arranger.apv.test;

import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FileHelper;

public class SaveConfigurationTest extends ConfigBasedTest {

	private static final String TEST_STORING_A_FEW_COLORS_CONF = "testStoringAFewColors.conf";
	private static final String TEST_GETTING_GLOBAL_CONFIG_CONF = "testGettingGlobalConfig.conf";
	
	
	public SaveConfigurationTest() {
	}

	@Test
	public void testConfiguratorSaveConfiguration() {
		cfg.saveCurrentConfig();
		filesToRemove.add(Configurator.APPLICATION_CONF_BAK);
	}
	
	@Test
	public void testGettingGlobalConfig() throws Exception {
		StringBuffer results = new StringBuffer();
		
		results.append("#Config saved on: " + new Timestamp(System.currentTimeMillis()).toString());
		results.append(System.lineSeparator()).append(System.lineSeparator());
		
		results.append(parent.getConfig()).append(System.lineSeparator());
		
		Arrays.asList(Main.SYSTEM_NAMES.values()).forEach(s -> {
			results.append(getConfigForSystem(s));
		});
		
		saveConfigFile(TEST_GETTING_GLOBAL_CONFIG_CONF, results.toString());
	}
	
	protected String getConfigForSystem(Main.SYSTEM_NAMES systemName) {
		List<? extends APVPlugin> ss = cfg.loadAVPPlugins(systemName);
		StringBuffer buffer = new StringBuffer();
		List<String> systems = new ArrayList<String>();
		buffer.append(systemName + " : [").append(System.lineSeparator());
		for (Iterator<? extends APVPlugin> it = ss.iterator(); it.hasNext();) {
			APVPlugin next = it.next();
			systems.add("     " + next.getConfigEx());
		}
		
		//sort the lines
		systems.sort(Comparator.naturalOrder());
		
		String result = systems.stream().collect(Collectors.joining(System.lineSeparator()));
		buffer.append(result);
		buffer.append(System.lineSeparator()).append("]").append(System.lineSeparator()).append(System.lineSeparator());
		return buffer.toString();
	}
	
	@Test
	public void testStoringAFewColors() throws Exception {
		List<? extends APVPlugin> ss = cfg.loadAVPPlugins(Main.SYSTEM_NAMES.COLORS);
		assert(ss != null);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("colors : [").append(System.lineSeparator());
		for (Iterator<? extends APVPlugin> it = ss.iterator(); it.hasNext();) {
			APVPlugin next = it.next();
			String pluginConfig = next.getConfigEx();
			buffer.append("   " + pluginConfig).append("\n");
		}
		buffer.append(System.lineSeparator()).append("]").append(System.lineSeparator());
		saveConfigFile(TEST_STORING_A_FEW_COLORS_CONF, buffer.toString());
	}
	
	protected void saveConfigFile(String fileName, String text) {
		System.out.println("Attemptint to write: " + fileName + " to disk" + System.lineSeparator() + text);
		
		//write to disk with file name application-${apv.start.date}.conf
		FileHelper fileHelper = new FileHelper(parent);
		fileHelper.saveFile(fileName, text);
		
		//see if we can parse the text?
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileHelper.getFullPath(fileName));
			Configurator temp = new Configurator(parent, fis);
			System.out.println(temp);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			assert(false);
		}
		
		filesToRemove.add(fileName);
	}
	
}
