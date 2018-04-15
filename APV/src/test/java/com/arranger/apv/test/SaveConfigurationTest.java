package com.arranger.apv.test;

import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FileHelper;

public class SaveConfigurationTest extends APVPluginTest {

	Configurator cfg;
	
	public SaveConfigurationTest() {
	}

	
	@BeforeEach
	public void setupConfigurator() {
		cfg = new Configurator(parent);
	}
	
	@Override
	protected void setFrameIndexes() {

	}

	@Test
	public void testGettingGlobalConfig() throws Exception {
		StringBuffer results = new StringBuffer();
		
		results.append("#Config saved on: " + new Timestamp(System.currentTimeMillis()).toString());
		results.append(System.lineSeparator()).append(System.lineSeparator());
		
		results.append(getConfigForSystem("scenes"));
		results.append(getConfigForSystem("backgrounds"));
		results.append(getConfigForSystem("backDrops"));
		results.append(getConfigForSystem("foregrounds"));
		results.append(getConfigForSystem("locations"));
		results.append(getConfigForSystem("colors"));
		results.append(getConfigForSystem("controls"));
		results.append(getConfigForSystem("filters"));
		results.append(getConfigForSystem("transitions"));
		results.append(getConfigForSystem("messages"));
		results.append(getConfigForSystem("switches"));
		results.append(getConfigForSystem("pulse-listeners"));
		
		saveConfigFile("testGettingGlobalConfig.conf", results.toString());
	}
	
	protected String getConfigForSystem(String systemName) {
		List<? extends APVPlugin> ss = cfg.loadAVPPlugins(systemName);
		StringBuffer buffer = new StringBuffer();
		List<String> systems = new ArrayList<String>();
		buffer.append(systemName + " : [").append(System.lineSeparator());
		for (Iterator<? extends APVPlugin> it = ss.iterator(); it.hasNext();) {
			APVPlugin next = it.next();
			systems.add("     " + next.getConfig());
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
		List<? extends APVPlugin> ss = cfg.loadAVPPlugins("colors");
		assert(ss != null);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("colors : [").append(System.lineSeparator());
		for (Iterator<? extends APVPlugin> it = ss.iterator(); it.hasNext();) {
			APVPlugin next = it.next();
			String pluginConfig = next.getConfig();
			buffer.append("   " + pluginConfig).append("\n");
		}
		buffer.append(System.lineSeparator()).append("]").append(System.lineSeparator());
		saveConfigFile("testStoringAFewColors.conf", buffer.toString());
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
	}
	
}
