package com.arranger.apv.test;

import org.junit.jupiter.api.Test;

import com.arranger.apv.event.APVEvent;
import com.arranger.apv.event.APVEvent.EventHandler;

public class EventTest extends APVPluginTest {

	public EventTest() {
	}

	Object result;
	
	@Test
	public void testSubscribe() {
		
		testSubscribeToEvent(parent.getSetupEvent());
		testSubscribeToEvent(parent.getSceneCompleteEvent());
		testSubscribeToEvent(parent.getDrawEvent());
		testSubscribeToEvent(parent.getSparkEvent());
		testSubscribeToEvent(parent.getStrobeEvent());
		testSubscribeToEvent(parent.getCarnivalEvent());
		

	}
	
	protected void testSubscribeToEvent(APVEvent<EventHandler> event) {
		EventHandler register = event.register(() -> {
			result = new Object();
		});
		
		//fire the event
		event.fire();
		assert(result != null);
		
		result = null;
		
		//fire the event again
		event.fire();
		assert(result != null);
		
		result = null;
		event.unregister(register);
		
		//fire the event one last time.  This time, should remain null
		event.fire();
		assert(result == null);
		
	}
	
	
	@Override
	protected void setFrameIndexes() {
		frameIndexStart = 0;
		frameIndexEnd = 100;

	}

}
