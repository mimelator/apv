package com.arranger.apv.back;

import java.awt.Color;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class OscilatingBackDrop extends BackDropSystem {
	
	private static final int SPEED_LOW = 4;
	private static final int SPEED_HIGH = 12;
	private Color c1, c2;
	private float oscSpeed;

	public OscilatingBackDrop(Main parent, Color c1, Color c2) {
		super(parent);
		this.c1 = c1;
		this.c2 = c2;
		
		oscSpeed = parent.random(SPEED_LOW, SPEED_HIGH);
		
		parent.getColorHelper().register(getDisplayName(), c1, c2, (col1, col2) -> {
			this.c1 = col1;
			this.c2 = col2;
		});
	}
	
	public OscilatingBackDrop(Configurator.Context ctx) {
		this(ctx.getParent(),
				ctx.getColor(0, Color.BLACK),
				ctx.getColor(1, Color.BLACK));
	}
	
	@Override
	public String getDisplayName() {
		return super.getDisplayName() + id;
	}
	
	@Override
	public String getConfig() {
		//{OscilatingBackDrop: [WHITE, BLACK]}
		return String.format("{%s : [%s, %s]}", getName(), format(c1, true), format(c2, true));
	}	
	
	@Override
	public void drawBackground() {
		float amt = parent.oscillate(0, 1, oscSpeed);
		int lerpColor = parent.lerpColor(c1.getRGB(), c2.getRGB(), amt);
		parent.background(lerpColor);
		
		String pctString = String.format("%.0f%%", amt * 100);
		String colors = String.format("%s %s", format(c1, false), format(c2, false));
		parent.addSettingsMessage(" --colors: " + colors);
		parent.addSettingsMessage(" --current: " + format(new Color(lerpColor), false));
		parent.addSettingsMessage(" --pctString: " + pctString);
		parent.addSettingsMessage(" --oscSpeed: " + oscSpeed);
	}
	
	private String format(Color c, boolean addQuote) {
		return parent.format(c, addQuote);
	}
}
