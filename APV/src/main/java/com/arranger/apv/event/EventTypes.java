package com.arranger.apv.event;

public enum EventTypes {
	/**
	 * Invoked after Main#setup when the agents should initialize
	 */
	SETUP, 
	
	/**
	 * Invoked after the render of every render loop just before cleanup
	 */
	DRAW, 
	
	/**
	 * TODO
	 */
	K_SCOPE,
	
	
	SPARK, 
	CARNIVAL, 
	STROBE, 
	SCENE_COMPLETE, 
	SETLIST_COMPLETE, 
	STAR, //STAR isn't currently in use
	COMMAND_INVOKED, 
	APV_CHANGE, 
	TWIRL, 
	LOCATION, 
	RANDOM_MESSAGE,
	MARQUEE, 
	EARTHQUAKE, 
	WATERMARK, 
	COLOR_CHANGE, 
	SONG_START, 
	SET_PACK_START, 
	ALDA, 
	MOUSE_PULSE,
	TEMPO_CHANGE
}