package com.arranger.apv.util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

public class Configurator extends APVPlugin {

	public static final String APPLICATION_CONF_BAK = "application.conf.bak";
	private static final String SCRAMBLE_KEY = "apv.scrambleSystems";

	private static final Logger logger = Logger.getLogger(Configurator.class.getName());
	
	private Config conf;
	private RegisteredClasses registeredClasses;
	private boolean shouldScrambleInitialSystems;
	
	public class Context {
		public ConfigList argList;
		
		public Context(ConfigList configList) {
			this.argList = configList;
		}
		
		public Main getParent() {
			return parent;
		}
		
		public float getFloat(int index, float defaultVal) {
			if (argList.size() > index) {
				return Float.parseFloat(argList.get(index).unwrapped().toString());
			} else {
				return defaultVal;
			}
		}
		
		public int getInt(int index, int defaultVal) {
			if (argList.size() > index) {
				return Integer.parseInt(argList.get(index).unwrapped().toString());
			} else {
				return defaultVal;
			}
		}
		
		public String getString(int index, String defaultVal) {
			if (argList.size() > index) {
				return argList.get(index).unwrapped().toString();
			} else {
				return defaultVal;
			}
		}
		
		public String [] getStringArray(int index, String [] defaultVal) {
			if (argList.size() > index) {
				ConfigValue configValue = argList.get(index);
				ConfigList configList = (ConfigList)configValue;
				
				List<String> sa = new ArrayList<String>();
				
				for (Iterator<ConfigValue> it = configList.iterator(); it.hasNext();) {
					ConfigValue cv = it.next();
					sa.add(cv.unwrapped().toString());
				}
				
				return sa.toArray(new String[sa.size()]);	
				
			} else {
				return defaultVal;
			}
		}
		
		public List<Command> getCommandList(int index) {
			List<Command> results = new ArrayList<Command>();
			String [] cmdStrings = getStringArray(index, null);
			if (cmdStrings != null) {
				Arrays.asList(cmdStrings).forEach(cs -> {
					results.add(Command.valueOf(cs));
				});
			}
			
			return results;
		}
		
		public boolean getBoolean(int index, boolean defaultVal) {
			if (argList.size() > index) {
				return Boolean.valueOf(argList.get(index).unwrapped().toString()).booleanValue();
			} else {
				return defaultVal;
			}
		}
		
		public Color getColor(int index, Color defaultVal) {
			if (argList.size() > index) {
				String colorString = argList.get(index).unwrapped().toString();
				Color c = decode(colorString);
				if (c != null) {
					return c;
				}
			}
			return defaultVal;
		}
		
		private static final String DECODE_COLOR_REGEX = "\\Q(\\E(\\d+),\\s?(\\d+),\\s?(\\d+)\\Q)\\E";
		private final Pattern COLOR_PATTERN = Pattern.compile(DECODE_COLOR_REGEX);
		
		private Color decode(String colorName) {
			if (colorName.contains("(")) {
				Matcher matcher = COLOR_PATTERN.matcher(colorName);
				if (matcher.matches()) {
					String r = matcher.group(1);
					String g = matcher.group(2);
					String b = matcher.group(3);
					return new Color(Integer.parseInt(r), Integer.parseInt(g), Integer.parseInt(b));
				}
			}
			
			
			try {
			    Field field = Color.class.getField(colorName);
			    return (Color)field.get(null);
			} catch (Exception e) {
			    return null;
			}
		}
		
		public APVPlugin loadPlugin(int index) {
			if (argList.size() > index) {
				ConfigValue cv = argList.get(index); 
				ConfigObject obj = (ConfigObject)cv;
				if (obj.entrySet().isEmpty()) {
					return null; //No Plugins listed.  Just an empty object like: {}
				}
				
				Entry<String, ConfigValue> next = obj.entrySet().iterator().next();
				return loadPlugin(next.getKey(), (ConfigList)next.getValue());
			} else {
				return null;
			}
		}
		
		public APVPlugin loadPlugin(String className, ConfigList argList) {
			return Configurator.this.loadPlugin(className, argList);
		}
		
		public Configurator getConfigurator() {
			return Configurator.this;
		}
	}
	
	public Configurator(Main parent) {
		super(parent);
		registeredClasses = new RegisteredClasses(parent);
		reload();
	}
	
	public Configurator(Main parent, InputStream inputStream) {
		super(parent);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		conf = ConfigFactory.parseReader(reader);
		initScramble();
	}
	
	public void reload() {
		ConfigFactory.invalidateCaches();
		conf = ConfigFactory.load();
		initScramble();
	}
	
	public Config getRootConfig() {
		return conf;
	}
	
	public List<? extends APVPlugin> loadAVPPlugins(Main.SYSTEM_NAMES name) {
		return loadAVPPlugins(name, true);
	}
	
	public List<? extends APVPlugin> loadAVPPlugins(Main.SYSTEM_NAMES name, boolean allowScramble) {
		List<APVPlugin> systems = new ArrayList<APVPlugin>();
		
		List<? extends Config> scl = getSystemConfigList(name);
		for (Iterator<? extends Config> it = scl.iterator(); it.hasNext();) {
			Config wrapperObj = it.next();
			Entry<String, ConfigValue> configObj = wrapperObj.entrySet().iterator().next();
			String key = configObj.getKey();
			ConfigList argList = (ConfigList)configObj.getValue();
			
			if (key == null || key.length() == 0) {
				throw new RuntimeException("Unable to load plugin: " + name);
			}
			
			APVPlugin plugin = null;
			try {
				plugin = loadPlugin(key, argList);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
			if (plugin == null) {
				throw new RuntimeException("Unable to load plugin: " + name);
			}
			systems.add(plugin);
		}
		
		if (shouldScrambleInitialSystems && allowScramble) {
			Collections.shuffle(systems);
		}
		
		return systems;
	}
	
	public List<? extends Config> getSystemConfigList(Main.SYSTEM_NAMES systemName) {
		List<? extends Config> configList = conf.getConfigList(systemName.name);
		return configList;
	}
	
	public APVPlugin loadPlugin(String className, ConfigList argList) {
		logger.info(className);
		try {
			Class<?> pluginClass = registeredClasses.getClassMap().get(className);
			if (pluginClass == null) {
				throw new RuntimeException("Plugin not registered with Configurator: " + className);
			}
			
			//look for an official constructor
			Constructor<?> ctor = findConstructor(pluginClass, Context.class);
			if (ctor != null) {
				Context context = new Context(argList);
				return (APVPlugin) ctor.newInstance(context);
			} else {
				//look for the old style with no config
				ctor = findConstructor(pluginClass, Main.class);
				if (ctor != null) {
					return (APVPlugin)ctor.newInstance(parent);
				} else {
					return null;
				}
			}
		} catch (Throwable t) {
			if (!t.getCause().equals(t)) {
				t = t.getCause();
			}
			logger.log(Level.SEVERE, t.getMessage(), t);
			return null;
		}
	}
	
	public void saveCurrentConfig() {
		StringBuffer results = new StringBuffer();
		
		results.append("#Config saved on: " + new Timestamp(System.currentTimeMillis()).toString());
		results.append(System.lineSeparator()).append(System.lineSeparator());
		
		results.append(parent.getConfig()); //Constants
		
		Arrays.asList(Main.SYSTEM_NAMES.values()).forEach(s -> {
			if (!s.equals(Main.SYSTEM_NAMES.LIKED_SCENES)) { //Comes from another location
				results.append(getConfigForSystem(s));
			}
		});
		results.append(getConfigForPlugins(Main.SYSTEM_NAMES.LIKED_SCENES, parent.getLikedScenes()));
		
		new FileHelper(parent).saveFile(APPLICATION_CONF_BAK, results.toString());
	}

	private String getConfigForPlugins(Main.SYSTEM_NAMES systemName, List<? extends APVPlugin> pluginList) {
		
		StringBuffer buffer = new StringBuffer();
		List<String> systems = new ArrayList<String>();
		buffer.append(systemName.name + " : [").append(System.lineSeparator());
		for (Iterator<? extends APVPlugin> it = pluginList.iterator(); it.hasNext();) {
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
	
	private String getConfigForSystem(Main.SYSTEM_NAMES systemName) {
		return getConfigForPlugins(systemName, loadAVPPlugins(systemName));
	}
	
	private Constructor<?> findConstructor(Class<?> targetClass, Class<?> targetParamType) {
		
		for (Constructor<?> ctor : targetClass.getConstructors()) {
			Class<?>[] ctorTypes = ctor.getParameterTypes();
			
			if (ctorTypes.length != 1) {
				continue;
			}
			
			Class<?> type1 = ctorTypes[0];
			
			if (type1.getName().equals(targetParamType.getName())) {
				return ctor;
			}
		}
		return null;
	}
	
	protected void initScramble() {
		Config rc = getRootConfig();
		if (rc.hasPath(SCRAMBLE_KEY)) {
			shouldScrambleInitialSystems = rc.getBoolean(SCRAMBLE_KEY);
		}
	}
}
