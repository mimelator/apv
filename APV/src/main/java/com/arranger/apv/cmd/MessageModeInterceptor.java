package com.arranger.apv.cmd;

import java.util.ArrayList;
import java.util.List;

import com.arranger.apv.Main;
import com.arranger.apv.cmd.CommandSystem.MessageHandler;

public class MessageModeInterceptor extends CommandInterceptor {
	
	public static final char MESSAGE_ENTRY_KEY = '~';
	
	protected List<MessageHandler> messageHandlers = new ArrayList<MessageHandler>();
	protected StringBuffer messageText;

	public MessageModeInterceptor(Main parent) {
		super(parent);
	}
	
	public void registerMessageListeners(MessageHandler handler) {
		messageHandlers.add(handler);
	}
	
	@Override
	public String getHelpText() {
		return "~: MessageEntry: Updates the Next message that the Marquee-type listener will display";
	}
	
	@Override
	public boolean intercept(char key) {
		if (key == MESSAGE_ENTRY_KEY) {
			if (active) {
				onMessage(messageText.toString());
			} else {
				//brand new
				messageText = new StringBuffer();
			}
			active = !active;
			return true;
		}
		
		if (active) {
			if (key != 65535) { //Don't understand what meta character i should be looking for
				messageText.append(String.valueOf(key));
			}
			return true;
		}
		
		return false;
	}
	
	protected void onMessage(String text) {
		messageHandlers.stream().forEach(e -> e.onMessage(text));
	}
}