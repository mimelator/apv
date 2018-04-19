package com.arranger.apv.pl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FontHelper;
import com.arranger.apv.util.JSONQuoter;

import processing.core.PFont;

public class RandomMessagePL extends APVPlugin {
	
	/**
	 * @see http://upli.st/l/list-of-all-ascii-emoticons
	 */
	private static String [] DEFAULT_MESSGES = {":("};
	
	private List<String> msgList = null;
	private int pulsesToSkip = 1;
	
	public RandomMessagePL(Main parent, int pulsesToSkip) {
		this(parent, pulsesToSkip, DEFAULT_MESSGES);
	}
	
	public RandomMessagePL(Main parent, int pulsesToSkip, String [] messages) {
		super(parent);
		if (messages == null) {
			msgList = Arrays.asList(DEFAULT_MESSGES);
		} else {
			msgList = Arrays.asList(messages);
		}
		
		//prepare fonts
		parent.getSetupEvent().register(() -> {
			FontHelper fh = parent.getFontHelper();
			PFont font = fh.createFontForText(msgList.stream().collect(Collectors.joining()));
			fh.setDefaultFont(font);
		});
		
		//subscribe
		this.pulsesToSkip = pulsesToSkip;
		parent.getSetupEvent().register(() -> {
			parent.getPulseListener().registerHandler( ()-> { 
				parent.sendMessage(new String[]{getMessage()}); 
				}  
			, pulsesToSkip); 
		});
	}
	
	public RandomMessagePL(Configurator.Context ctx) {
		this(ctx.getParent(), ctx.getInt(0, 4), ctx.getStringArray(1, null));
	}
	
	@Override
	public String getConfig() {
		//{SimplePL : [2, ${emoji-messages}]}
		JSONQuoter quoter = new JSONQuoter(parent);
		String msgs = msgList.stream().map(e -> quoter.quote(e)).collect(Collectors.joining(","));
		
		return String.format("{%1s : [%2s, [%3s]]}", getName(), pulsesToSkip, msgs);
	}
	
	private String getMessage() {
		return msgList.get((int)parent.random(msgList.size()));
	}
}
