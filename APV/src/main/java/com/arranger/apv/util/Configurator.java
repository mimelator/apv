package com.arranger.apv.util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.cmd.Command;
import com.arranger.apv.helpers.HotKey;
import com.arranger.apv.helpers.HotKeyHelper;
import com.arranger.apv.helpers.Macro;
import com.arranger.apv.helpers.MacroHelper;
import com.arranger.apv.helpers.Switch;
import com.arranger.apv.shader.Shader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

public class Configurator extends APVPlugin {

	public static final String REFERENCE_CONF = "reference.conf";
	public static final String APPLICATION_CONF = "application.conf";
	public static final String APPLICATION_CONF_BAK = "application.conf.bak";
	public static final String DISABLED_PLUGIN_KEY = "disabledPlugins";
	private static final String SCRAMBLE_KEY = "apv.scrambleSystems";
	

	private static final Logger logger = Logger.getLogger(Configurator.class.getName());
	
	private JSONQuoter quoter;
	private Config conf;
	private RegisteredClasses registeredClasses;
	private boolean shouldScrambleInitialSystems;
	private ColorHelper colorHelper;
	private Set<String> disabledPlugins;
	
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
		
		public Main.SYSTEM_NAMES getSystemName(int index, Main.SYSTEM_NAMES defaultVal) {
			if (argList.size() > index) {
				String res = getString(index, null);
				if (res == null) {
					return defaultVal;
				}
				return Main.SYSTEM_NAMES.valueOf(res);
			}
			return defaultVal;
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
		
		public Color [] getColorArray(int index) {
			List<Color> colorList = new ArrayList<Color>();
			while (index < argList.size()) {
				colorList.add(getColor(index, null));
				index++;
			}
			
			Color [] results = new Color[colorList.size()];
			IntStream.range(0, results.length).forEach(i -> {
				results[i] = colorList.get(i);
			});
			
			
			return results;
		}

		public float [] getFloatArray(int index) {
			List<Float> floatList = new ArrayList<Float>();
			while (index < argList.size()) {
				floatList.add(getFloat(index, 0));
				index++;
			}
			
			float [] results = new float[floatList.size()];
			IntStream.range(0, results.length).forEach(i -> {
				results[i] = floatList.get(i);
			});
			return results;
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
		
		
		/**
		 * I'd like to refactor this and {@link #getCommandList(int)} into a generic function
		 */
		public List<Shader.SHADERS> getShaderList(int index) {
			List<Shader.SHADERS> results = new ArrayList<Shader.SHADERS>();
			String [] cmdStrings = getStringArray(index, null);
			if (cmdStrings != null) {
				Arrays.asList(cmdStrings).forEach(cs -> {
					results.add(Shader.SHADERS.valueOf(cs.toUpperCase()));
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
				ColorHelper ch = getColorHelper();
				Color c = ch.decode(colorString);
				if (c != null) {
					return c;
				}
			}
			return defaultVal;
		}
		
		public List<? extends APVPlugin> loadRemainingPlugins(int index) {
			List<APVPlugin> plugins = new ArrayList<APVPlugin>();
			
			while (index < argList.size()) {
				plugins.add(loadPlugin(index));
				index++;
			}
			
			return plugins;
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
		
		protected ColorHelper getColorHelper() {
			if (colorHelper == null) {
				colorHelper = new ColorHelper(parent);
			}
			return colorHelper;
		}
	}
	
	public Configurator(Main parent) {
		super(parent);
		quoter = new JSONQuoter(parent);
		registeredClasses = new RegisteredClasses(parent);
		reload();
	}
	
	public Configurator(Main parent, InputStream inputStream) {
		super(parent);
		quoter = new JSONQuoter(parent);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		conf = ConfigFactory.parseReader(reader);
		initScramble();
	}
	
	public void reload() {
		reload(null);
	}
	
	public void reload(String file) {
		ConfigFactory.invalidateCaches();
		if (file != null) {
			System.setProperty("config.file", file);
		}
		conf = ConfigFactory.load();
		initScramble();
	}
	
	public Config getRootConfig() {
		return conf;
	}
	
	public APVPlugin loadObjectFromConfig(String config) {
		Config tempConf = ConfigFactory.parseReader(new StringReader(config));  
		Entry<String, ConfigValue> entry = tempConf.entrySet().iterator().next();
		String key = entry.getKey();
		ConfigList argList = (ConfigList)entry.getValue();
		return loadPlugin(key, argList);
	}
	
	public List<? extends APVPlugin> loadAVPPlugins(Main.SYSTEM_NAMES name) {
		return loadAVPPlugins(name, true);
	}
	
	public List<? extends APVPlugin> loadAVPPlugins(Main.SYSTEM_NAMES name, boolean allowScramble) {
		List<APVPlugin> systems = new ArrayList<APVPlugin>();
		
		List<? extends Config> scl = getSystemConfigList(name);
		scl.forEach(wrapperObj -> {
			Entry<String, ConfigValue> configObj = wrapperObj.entrySet().iterator().next();
			String key = configObj.getKey();
			ConfigList argList = (ConfigList)configObj.getValue();
			
			if (key == null || key.length() == 0) {
				throw new RuntimeException("Unable to load plugin: " + name);
			}
			
			if (!isDisabled(key)) {
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
				
				//is plugin display name on the disabled list?
				if (!isDisabled(plugin)) {
					systems.add(plugin);
				}
			}
		});
		
		if (shouldScrambleInitialSystems && allowScramble) {
			Collections.shuffle(systems);
		}
		
		return systems;
	}
	
	protected boolean isDisabled(String className) {
		Set<String> disabledSet = getDisabledPluginSet();
		
		boolean contains = disabledSet.contains(className);
		if (contains) {
			System.out.println("Not loading disabled plugin: " + className);
		}
		return contains;
	}
	
	protected boolean isDisabled(APVPlugin plugin) {
		Set<String> disabledSet = getDisabledPluginSet();
		
		String pluginDisplayName = plugin.getDisplayName();
		boolean contains = disabledSet.contains(pluginDisplayName);
		if (contains) {
			System.out.println("Not loading disabled plugin: " + pluginDisplayName);
		}
		return contains;
	}

	protected Set<String> getDisabledPluginSet() {
		if (disabledPlugins == null) {
			List<String> stringList = parent.getConfigurator().getRootConfig().getStringList(DISABLED_PLUGIN_KEY);
			disabledPlugins = new HashSet<String>(stringList);
		}
		return disabledPlugins;
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
		saveCurrentConfig(true);
	}
	
	public void saveCurrentConfig(boolean alsoSaveOrig) {
		saveConfigImpl(new File(APPLICATION_CONF), alsoSaveOrig);
	}
	
	public void saveCurrentConfig(File targetConfig) {
		saveConfigImpl(targetConfig, false);
	}
	
	protected void saveConfigImpl(File f, boolean alsoSaveOrig) {
		String results = generateCurrentConfig();
		
		//save application.conf.bak
		FileHelper fh = new FileHelper(parent);
		fh.saveFile(f.getAbsolutePath(), results.toString());
		
		//save reference.conf
		if (alsoSaveOrig) {
			fh.getResourceAsStream(REFERENCE_CONF, (stream) -> {
				FileUtils.copyInputStreamToFile(stream, new File(fh.getFullPath(REFERENCE_CONF)));
			});
		}
	}

	public String generateCurrentConfig() {
		StringBuffer results = new StringBuffer();
		results.append("#Config saved on: " + new Timestamp(System.currentTimeMillis()).toString());
		results.append(System.lineSeparator()).append(System.lineSeparator());
		results.append(parent.getConfig()); //Constants
		Arrays.asList(Main.SYSTEM_NAMES.values()).forEach(s -> {
			results.append(getConfigForSystem(s));
		});
		return results.toString();
	}
	
	public String generateConfig(String name, List<String> entries, boolean sort, boolean shouldQuote) {
		StringBuffer buffer = new StringBuffer();
		List<String> resultList = new ArrayList<String>();
		buffer.append(name + " : [").append(System.lineSeparator());
		entries.forEach(entry -> {
			if (shouldQuote) {
				resultList.add("     " + quoter.quote(entry));
			} else {
				resultList.add("     " + entry);
			}
		});
		
		//sort the lines
		if (sort) {
			resultList.sort(Comparator.naturalOrder());
		}
		
		String result = resultList.stream().collect(Collectors.joining(System.lineSeparator()));
		buffer.append(result);
		buffer.append(System.lineSeparator()).append("]").append(System.lineSeparator()).append(System.lineSeparator());
		return buffer.toString();
	}
	
	private String getConfigForSystem(Main.SYSTEM_NAMES systemName) {
		List<? extends APVPlugin> pluginList = null;
		switch (systemName) {
			case AGENTS:
				pluginList = parent.getAgent().getList();
				break;
			case PULSELISTENERS:
				pluginList = parent.getPulseListener().getList();
				break;
			case WATERMARKS:
				pluginList = parent.getWatermark().getList();
				break;
			case HOTKEYS:
				HotKeyHelper hotKeyHelper = parent.getHotKeyHelper();
				pluginList = new ArrayList<HotKey>(hotKeyHelper.getHotKeys().values());
				break;
			case MACROS:
				MacroHelper macroHelper = parent.getMacroHelper();
				pluginList = new ArrayList<Macro>(macroHelper.getMacros().values());
				break;
			case SWITCHES:
				pluginList = new ArrayList<Switch>(parent.getSwitches().values());
				break;
			case LIKED_SCENES:
				pluginList = parent.getLikedScenes();
				break;
			default:
				if (systemName.isFullSystem) {
					pluginList = parent.getSystem(systemName).getList();
				} else {
					throw new RuntimeException("Unknown non full system: " + systemName.name);
				}
		}
		return getConfigForPlugins(systemName, pluginList);
	}

	private String getConfigForPlugins(Main.SYSTEM_NAMES systemName, List<? extends APVPlugin> list) {
		return generateConfig(systemName.name, 
				list.stream().map(p -> p.getConfig()).collect(Collectors.toList()), 
				true,
				false);
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
