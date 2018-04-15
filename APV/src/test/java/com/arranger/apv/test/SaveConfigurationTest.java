package com.arranger.apv.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FileHelper;

public class SaveConfigurationTest extends APVPluginTest {

	public SaveConfigurationTest() {
	}

	@Override
	protected void setFrameIndexes() {

	}

	
	@Test
	public void testClassLoading() throws Exception {
		Configurator cfg = new Configurator(parent);
		
		List<? extends APVPlugin> ss = cfg.loadAVPPlugins("colors");
		assert(ss != null);
		
		StringBuffer buffer = new StringBuffer();
		for (Iterator<? extends APVPlugin> it = ss.iterator(); it.hasNext();) {
			APVPlugin next = it.next();
			String pluginConfig = next.getConfig();
			buffer.append(pluginConfig).append("\n");
		}
		
		//write to disk with file name application-${apv.start.date}.conf
		FileHelper fileHelper = new FileHelper(parent);
		String fullPath = fileHelper.getFullPath("application.conf");
		BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath));
		writer.write(buffer.toString());
		writer.close();
	}
}
