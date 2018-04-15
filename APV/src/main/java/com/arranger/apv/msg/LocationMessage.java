package com.arranger.apv.msg;

import com.arranger.apv.Main;
import com.arranger.apv.MessageSystem;
import com.arranger.apv.util.Configurator;

public class LocationMessage extends MessageSystem {
	
	private static final int TEXT_SIZE = 50;

	public enum CORNER_LOCATION {
		UPPER_LEFT, UPPER_RIGHT, LOWER_LEFT, LOWER_RIGHT
		};
	
	private CORNER_LOCATION cornerLocation = CORNER_LOCATION.UPPER_LEFT;
	
	public LocationMessage(Main parent, CORNER_LOCATION cornerLocation) {
		super(parent);
		this.cornerLocation = cornerLocation;
	}
	
	public LocationMessage(Configurator.Context ctx) {
		this(ctx.getParent(), 
				CORNER_LOCATION.valueOf(
						ctx.getString(0, CORNER_LOCATION.UPPER_LEFT.name())));
	}

	@Override
	public String getConfig() {
		//{LocationMessage : [UPPER_LEFT]}
		return String.format("{%1s : [%2s]}", getName(), cornerLocation.name());
	}
	
	@Override
	public String getDisplayName() {
		return String.format("%1s[%2s]", super.getDisplayName(), cornerLocation.name());
	}

	@Override
	protected void _draw(FadingMessage fadingMessage) {
		parent.textSize(TEXT_SIZE);
		String message = joinMessage(fadingMessage, ":");
		
		doStandardFade(1.0f);
		
		float x = 0, y = 0;
		switch (cornerLocation) {
		case UPPER_LEFT:
			parent.textAlign(LEFT);
			x = parent.width * .05f;
			y = parent.height * .1f;
			break;
		case LOWER_LEFT:
			parent.textAlign(LEFT);
			x = parent.width * .05f;
			y = parent.height * .9f;
			break;
		case UPPER_RIGHT:
			parent.textAlign(RIGHT);
			x = parent.width * .65f;
			y = parent.height * .1f;
			break;
		case LOWER_RIGHT:
			parent.textAlign(RIGHT);
			x = parent.width * .65f;
			y = parent.height * .9f;
			break;
		}
		
		parent.text(message, x, y);
	}

}
