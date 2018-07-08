package com.arranger.apv.archive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;


public class DCollector {

	//TODO check reference.conf
	public static String [] UNIQUE_FLAGS = {"defaultCommands", "disabledPlugins", "apvMessages"};
	
	
	public static void main(String[] args) {
		
		Config config = ConfigFactory.load();
		List<String> cmds = new ArrayList<String>();
		
		IntStream.range(0, UNIQUE_FLAGS.length).forEach(i -> {
			String uf = UNIQUE_FLAGS[i];
			if (config.hasPath(uf)) {
				Iterator<ConfigValue> iterator = null;
				
				ConfigValue value = config.getValue(uf);
				if (value instanceof ConfigList) {
					ConfigList cl = (ConfigList)value;
					iterator = cl.iterator();
				} else {
					ConfigObject configObject = config.getObject(uf);
					Collection<ConfigValue> values = configObject.values();
					iterator = values.iterator();
				}
				
				for (int configIndex = 0; iterator.hasNext(); configIndex++) {
					ConfigValue next = iterator.next();
					String format = String.format("-D%s.%s=%s", uf, configIndex, next.unwrapped());
					cmds.add(format);
				}
			}
		});
		
		System.out.print(cmds);
	}

}
