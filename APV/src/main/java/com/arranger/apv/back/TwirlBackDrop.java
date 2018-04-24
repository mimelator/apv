package com.arranger.apv.back;

import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;

public class TwirlBackDrop extends BackDropSystem {

	private static final float DEFAULT_SPEED = 10;
	private static final float DEFAULT_ROTATION_AMOUNT = TWO_PI * 5;
	
	private float speed;
	private float duration;
	
	public TwirlBackDrop(Main parent, float speed, float duration) {
		super(parent);
		this.speed = speed;
		this.duration = duration;
	}
	
	public TwirlBackDrop(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getFloat(0, DEFAULT_SPEED), ctx.getFloat(1, DEFAULT_ROTATION_AMOUNT));
	}
	
	@Override
	public String getConfig() {
		//{TwirlBackDrop : [10, 100]}
		return String.format("{%s : [%f, %f]}", getName(), speed, duration);
	}

	@Override
	public void drawBackground() {
		int x = parent.width / 2;
		int y = parent.height / 2;
		parent.translate(x, y);
		float oscillate = parent.oscillate(0f, duration, speed, () -> {
			parent.getTwirlEvent().fire();
		});
		parent.rotate(oscillate);
		parent.translate(-x, -y);
	}
	
	/**
	 * Don't overwrite the rotation
	 */
	public boolean isSafe() {
		return false;
	}
}
