package com.arranger.apv.util;

import java.util.HashMap;
import java.util.Map;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.Scene;
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
import com.arranger.apv.factory.CircleImageFactory;
import com.arranger.apv.factory.DotFactory;
import com.arranger.apv.factory.ParametricFactory.HypocycloidFactory;
import com.arranger.apv.factory.SpriteFactory;
import com.arranger.apv.factory.SquareFactory;
import com.arranger.apv.factory.StarFactory;
import com.arranger.apv.filter.BlendModeFilter;
import com.arranger.apv.filter.Filter;
import com.arranger.apv.filter.PulseShakeFilter;
import com.arranger.apv.filter.StrobeFilter;
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
import com.arranger.apv.scene.Marquee;
import com.arranger.apv.scene.Tree;
import com.arranger.apv.systems.lifecycle.GravitySystem;
import com.arranger.apv.systems.lifecycle.RotatorSystem;
import com.arranger.apv.systems.lifecycle.WarpSystem;
import com.arranger.apv.systems.lite.AttractorSystem;
import com.arranger.apv.systems.lite.BGImage;
import com.arranger.apv.systems.lite.BlockStep;
import com.arranger.apv.systems.lite.BoxWaves;
import com.arranger.apv.systems.lite.FloatingGeometry;
import com.arranger.apv.systems.lite.GridShapeSystem;
import com.arranger.apv.systems.lite.LightWormSystem;
import com.arranger.apv.systems.lite.PixelAttractor;
import com.arranger.apv.systems.lite.PlasmaSystem;
import com.arranger.apv.systems.lite.Spirograph;
import com.arranger.apv.systems.lite.SpraySpark;
import com.arranger.apv.systems.lite.TwistedLines;
import com.arranger.apv.systems.lite.cycle.BubbleShapeSystem;
import com.arranger.apv.systems.lite.cycle.CarnivalShapeSystem;
import com.arranger.apv.systems.lite.cycle.ScribblerShapeSystem;
import com.arranger.apv.systems.lite.cycle.StarWebSystem;
import com.arranger.apv.transition.Fade;
import com.arranger.apv.transition.Shrink;
import com.arranger.apv.transition.Swipe;
import com.arranger.apv.transition.Twirl;

public class RegisteredClasses extends APVPlugin {

	public RegisteredClasses(Main parent) {
		super(parent);
	}

	public Map<String, Class<?>> getClassMap() {
		return CLASS_MAP;
	}
	
	private static Map<String, Class<?>> CLASS_MAP = new HashMap<String, Class<?>>();

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
		PlasmaSystem.class,
		StarWebSystem.class,
		GravitySystem.class,
		RotatorSystem.class,
		CarnivalShapeSystem.class,
		FloatingGeometry.class,
		TwistedLines.class,
		SpraySpark.class,
		BlockStep.class,
		
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
		StrobeFilter.class,
		
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
		
		//Scenes
		Scene.class,
		Marquee.class,
		Tree.class,
	};
	
	static { for (Class<?> cls : CLASSES) {CLASS_MAP.put(cls.getSimpleName(), cls);} }
}
