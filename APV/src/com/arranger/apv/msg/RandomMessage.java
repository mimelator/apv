package com.arranger.apv.msg;

import com.arranger.apv.Main;
import com.arranger.apv.MessageSystem;

public class RandomMessage extends MessageSystem {

	private static final int MAX_TEXT_SIZE = 150;

	public RandomMessage(Main parent) {
		super(parent);
	}

	@Override
	protected void _draw(FadingMessage fadingMessage) {
		Main p = parent;
		
		p.textAlign(CENTER, CENTER);
		p.rotate(p.random(-1, +1)); // rotate text 
		p.fill(p.getColorSystem().getCurrentColor().getRGB());
		p.textSize(p.random(MAX_TEXT_SIZE));
		
		for (String msg : fadingMessage.messages) {
			p.text(msg, p.random(p.width * .8f), p.random(p.height * .8f));
		}
	}
}
