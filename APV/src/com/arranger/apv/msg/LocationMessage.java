package com.arranger.apv.msg;

import com.arranger.apv.Main;
import com.arranger.apv.MessageSystem;

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

	@Override
	public String getDisplayName() {
		return super.getDisplayName() + "[" + cornerLocation.name() + "]";
	}

	@Override
	protected void _draw(FadingMessage fadingMessage) {
		parent.textSize(TEXT_SIZE);
		String message = joinMessage(fadingMessage, ":");
		
		doStandardFade(1.0f);
		
		float x = 0, y = 0;
		switch (cornerLocation) {
		case UPPER_LEFT:
			parent.textMode(LEFT);
			x = parent.width * .05f;
			y = parent.height * .1f;
			break;
		case LOWER_LEFT:
			parent.textMode(LEFT);
			x = parent.width * .05f;
			y = parent.height * .9f;
			break;
		case UPPER_RIGHT:
			parent.textMode(RIGHT);
			x = parent.width * .65f;
			y = parent.height * .1f;
			break;
		case LOWER_RIGHT:
			parent.textMode(RIGHT);
			x = parent.width * .65f;
			y = parent.height * .9f;
			break;
		}
		
		parent.text(message, x, y);
	}

}
