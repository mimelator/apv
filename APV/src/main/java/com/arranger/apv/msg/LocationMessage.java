package com.arranger.apv.msg;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.draw.SafePainter;

public class LocationMessage extends MessageSystem {
	
	private static final int TEXT_SIZE = 50;
	
	private SafePainter.LOCATION cornerLocation = SafePainter.LOCATION.NONE;
	
	public LocationMessage(Main parent, SafePainter.LOCATION cornerLocation) {
		super(parent);
		this.cornerLocation = cornerLocation;
	}
	
	public LocationMessage(Configurator.Context ctx) {
		this(ctx.getParent(), 
				SafePainter.LOCATION.valueOf(
						ctx.getString(0, SafePainter.LOCATION.NONE.name())));
	}

	@Override
	public String getConfig() {
		//{LocationMessage : [UPPER_LEFT]}
		return String.format("{%s : [%s]}", getName(), cornerLocation.name());
	}
	
	@Override
	public String getDisplayName() {
		return String.format("%s[%s]", super.getDisplayName(), cornerLocation.name());
	}

	@Override
	protected void _draw(FadingMessage fm) {
		String message = joinMessage(fm, ":");
		
		new SafePainter(parent, () ->  {
			doStandardFade(1.0f);
			parent.textSize(TEXT_SIZE);
			parent.text(message, 0, 0);
		}).paint(fm.location);
	}
	
	@Override
	protected void onCreatedFadingMessage(FadingMessage fadingMessage) {
		fadingMessage.location = calculateLocation();
	}
	
	private SafePainter.LOCATION calculateLocation() {
		if (!SafePainter.LOCATION.NONE.equals(cornerLocation)) {
			return cornerLocation;
		}
		
		SafePainter.LOCATION result = null;
		while (result == null) {
			result = SafePainter.LOCATION.random();
			if (SafePainter.LOCATION.NONE.equals(result)) {
				result = null;
			}
		}
		
		return result;
	}
}
