package com.arranger.apv.back;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class OscilatingBackDrop extends BackDropSystem {
	
	private static final int SPEED_LOW = 4;
	private static final int SPEED_HIGH = 12;
	private Color c1, c2;
	private float oscSpeed;
	private String displayName;

	public OscilatingBackDrop(Main parent, Color c1, Color c2) {
		super(parent);
		this.c1 = c1;
		this.c2 = c2;
		
		displayName = format(c1) + " " + format(c2);
		oscSpeed = parent.random(SPEED_LOW, SPEED_HIGH);
	}
	
	public OscilatingBackDrop(Configurator.Context ctx) {
		this(ctx.getParent(),
				ctx.getColor(0, Color.BLACK),
				ctx.getColor(1, Color.BLACK));
	}
	

	@Override
	public String getConfig() {
		//{OscilatingBackDrop: [WHITE, BLACK, White-Black]}
		String name = getName();
		return String.format("{%1s : [%2s, %3s, %4s]}", name, format(c1), format(c2), displayName);
	}	
	
	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void drawBackground() {
		float amt = parent.oscillate(0, 1, oscSpeed);
		int lerpColor = parent.lerpColor(c1.getRGB(), c2.getRGB(), amt);
		parent.background(lerpColor);
		
		String pctString = String.format("%.0f%%", amt * 100);
		String colors = String.format("%1s %2s", format(c1), format(c2));
		parent.addSettingsMessage(" --colors: " + colors);
		parent.addSettingsMessage(" --current: " + format(new Color(lerpColor)));
		parent.addSettingsMessage(" --pctString: " + pctString);
		parent.addSettingsMessage(" --oscSpeed: " + oscSpeed);
	}
	
	private String format(Color c) {
		return String.format("(%1s,%2s,%3s)", c.getRed(), c.getGreen(), c.getBlue());
	}
}
