package com.arranger.apv.gradient;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class GradientHelperFractions extends APVPlugin {
	
	private float [] fractions;

	public GradientHelperFractions(Main parent, float [] fractions) {
		super(parent);
		this.fractions = fractions;
	}
	
	public GradientHelperFractions(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getFloatArray(0));
	}

	@Override
	public String getConfig() {
		//{GradientHelperFractions : [0, .1, 1]}
		StringBuffer buffer = new StringBuffer();
		for (int index = 0; index < fractions.length; index++) {
			buffer.append(fractions[index]);
			if (index < fractions.length - 1) {
				buffer.append(", ");
			}
		}
		return String.format("{%s : [%s]}", getName(), buffer.toString());
	}

	public float[] getFractions() {
		return fractions;
	}

	public void setFractions(float[] fractions) {
		this.fractions = fractions;
	}
}
