package com.arranger.apv.util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.Switch;
import com.arranger.apv.audio.FreqDetector;
import com.arranger.apv.back.BackDropSystem;
import com.arranger.apv.back.BlurBackDrop;
import com.arranger.apv.back.OscilatingBackDrop;
import com.arranger.apv.back.PulseRefreshBackDrop;
import com.arranger.apv.back.RefreshBackDrop;
import com.arranger.apv.color.BeatColorSystem;
import com.arranger.apv.color.OscillatingColor;
import com.arranger.apv.color.RandomColor;
import com.arranger.apv.control.Auto;
import com.arranger.apv.control.Manual;
import com.arranger.apv.control.Perlin;
import com.arranger.apv.control.Snap;
import com.arranger.apv.factories.CircleImageFactory;
import com.arranger.apv.factories.DotFactory;
import com.arranger.apv.factories.ParametricFactory.HypocycloidFactory;
import com.arranger.apv.factories.SpriteFactory;
import com.arranger.apv.factories.SquareFactory;
import com.arranger.apv.factories.StarFactory;
import com.arranger.apv.filter.BlendModeFilter;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.filter.PulseShakeFilter;
import com.arranger.apv.loc.CircularLocationSystem;
import com.arranger.apv.loc.MouseLocationSystem;
import com.arranger.apv.loc.PerlinNoiseWalkerLocationSystem;
import com.arranger.apv.loc.RectLocationSystem;
import com.arranger.apv.msg.CircularMessage;
import com.arranger.apv.msg.LocationMessage;
import com.arranger.apv.msg.RandomMessage;
import com.arranger.apv.msg.StandardMessage;
import com.arranger.apv.pl.SimplePL;
import com.arranger.apv.pl.StarPL;
import com.arranger.apv.systems.lifecycle.GravitySystem;
import com.arranger.apv.systems.lifecycle.RotatorSystem;
import com.arranger.apv.systems.lifecycle.WarpSystem;
import com.arranger.apv.systems.lite.AttractorSystem;
import com.arranger.apv.systems.lite.BGImage;
import com.arranger.apv.systems.lite.BoxWaves;
import com.arranger.apv.systems.lite.GridShapeSystem;
import com.arranger.apv.systems.lite.LightWormSystem;
import com.arranger.apv.systems.lite.PixelAttractor;
import com.arranger.apv.systems.lite.PlasmaSystem;
import com.arranger.apv.systems.lite.ShowerSystem;
import com.arranger.apv.systems.lite.Spirograph;
import com.arranger.apv.systems.lite.cycle.BubbleShapeSystem;
import com.arranger.apv.systems.lite.cycle.CarnivalShapeSystem;
import com.arranger.apv.systems.lite.cycle.ScribblerShapeSystem;
import com.arranger.apv.systems.lite.cycle.StarWebSystem;
import com.arranger.apv.transition.Fade;
import com.arranger.apv.transition.Shrink;
import com.arranger.apv.transition.Swipe;
import com.arranger.apv.transition.Twirl;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

public class Configurator extends APVPlugin {

public static Map<String, Class<?>> CLASS_MAP = new HashMap<String, Class<?>>();

	private static final Logger logger = Logger.getLogger(Configurator.class.getName());

	private static final Class<?> [] CLASSES = new Class<?>[] {
		//Shape Systems
		BGImage.class,
		BoxWaves.class,
		Spirograph.class,
		PixelAttractor.class,
		WarpSystem.class,
		AttractorSystem.class,
		BubbleShapeSystem.class, 
		AttractorSystem.class, 
		FreqDetector.class,
		GridShapeSystem.class,
		BubbleShapeSystem.class,
		LightWormSystem.class,
		ScribblerShapeSystem.class,
		GridShapeSystem.class,
		ShowerSystem.class,
		PlasmaSystem.class,
		StarWebSystem.class,
		GravitySystem.class,
		RotatorSystem.class,
		CarnivalShapeSystem.class,
		
		
		//factories
		SquareFactory.class,
		CircleImageFactory.class,
		SpriteFactory.class,
		DotFactory.class,
		StarFactory.class,
		HypocycloidFactory.class,
		
		//BackDrop Systems
		BackDropSystem.class,
		OscilatingBackDrop.class,
		PulseRefreshBackDrop.class,
		OscilatingBackDrop.class,
		RefreshBackDrop.class,
		BlurBackDrop.class,
		
		//Filters
		Filter.class,
		PulseShakeFilter.class,
		BlendModeFilter.class,
		
		//Transitions
		Twirl.class,
		Shrink.class,
		Fade.class,
		Swipe.class,

		//Messages
		LocationMessage.class,
		CircularMessage.class,
		RandomMessage.class,
		StandardMessage.class,

		//Location
		CircularLocationSystem.class,
		PerlinNoiseWalkerLocationSystem.class,
		MouseLocationSystem.class,
		RectLocationSystem.class,

		//Color
		BeatColorSystem.class,
		OscillatingColor.class,
		RandomColor.class,

		//Control
		Manual.class,
		Auto.class,
		Snap.class,
		Perlin.class,
		
		//Switches
		Switch.class,
		
		//Listeners
		SimplePL.class,
		StarPL.class, 
	};
	
	static { for (Class<?> cls : CLASSES) {CLASS_MAP.put(cls.getSimpleName(), cls);} }
	
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
		
		private Color decode(String colorName) {
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
	
	private Config conf;
	
	public Configurator(Main parent) {
		super(parent);
		conf = ConfigFactory.load();
	}
	
	public Configurator(Main parent, InputStream inputStream) {
		super(parent);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		conf = ConfigFactory.parseReader(reader);
	}
	
	public Config getRootConfig() {
		return conf;
	}
	
	public List<? extends APVPlugin> loadAVPPlugins(String name) {
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
		
		return systems;
	}
	
	public List<? extends Config> getSystemConfigList(String systemName) {
		List<? extends Config> configList = conf.getConfigList(systemName);
		return configList;
	}
	
	public APVPlugin loadPlugin(String className, ConfigList argList) {
		logger.info(className);
		try {
			Class<?> pluginClass = CLASS_MAP.get(className);
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
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
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
	
}
