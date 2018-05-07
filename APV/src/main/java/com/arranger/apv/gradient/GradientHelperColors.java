package com.arranger.apv.gradient;

import java.awt.Color;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class GradientHelperColors extends APVPlugin {

	private Color [] colors;
	
	public GradientHelperColors(Main parent, Color [] colors) {
		super(parent);
		this.colors = colors;
	}
	
	public GradientHelperColors(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getColorArray(0));
	}

	@Override
	public String getConfig() {
		//{GradientHelperColors : [BLACK, "(128, 0, 128)", RED]}
		String colorConfig = Arrays.asList(colors).stream().map(c -> parent.format(c, true)).collect(Collectors.joining(","));
		return String.format("{%s : [%s]}", getName(), colorConfig);
	}



	public Color[] getColors() {
		return colors;
	}

	public void setColors(Color[] colors) {
		this.colors = colors;
	}

	
}
