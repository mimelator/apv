package com.arranger.apv.msg;

import java.awt.geom.Point2D;

import com.arranger.apv.Main;
import com.arranger.apv.MessageSystem;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.SafePainter;

public class LocationMessage extends MessageSystem {
	
	private static final int TEXT_SIZE = 50;
	private static final int OFFSET = SafePainter.OFFSET * 5;
	
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
	protected void _draw(FadingMessage fadingMessage) {
		String message = joinMessage(fadingMessage, ":");
		
		new SafePainter(parent, () ->  {
			Point2D offset = getOffset(fadingMessage.location);
			doStandardFade(1.0f);
			parent.textSize(TEXT_SIZE);
			parent.textAlign(cornerLocation.getAlignment());
			parent.text(message, (int)offset.getX(), (int)offset.getY());
		}).paint(fadingMessage.location);
	}
	
	@Override
	protected FadingMessage createFadingMessage(String[] messages) {
		FadingMessage fm = super.createFadingMessage(messages);
		fm.location = calculateLocation();
		return fm;
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
	
	private Point2D getOffset(SafePainter.LOCATION loc) {
		float x = 0, y = 0;
		switch (cornerLocation) {
		case UPPER_LEFT:
			x = OFFSET;
			y = OFFSET;
			break;
		case UPPER_RIGHT:
			x = -OFFSET;
			y = OFFSET;
			break;
		case LOWER_RIGHT:
			x = -OFFSET;
			y = -OFFSET;
			break;
		case LOWER_LEFT:
			x = OFFSET;
			y = -OFFSET;
			break;
		case NONE:
			default:
		}
		return new Point2D.Float(x, y);
	}
}
