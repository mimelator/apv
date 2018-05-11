package com.arranger.apv.test;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main.SYSTEM_NAMES;
import com.arranger.apv.util.Configurator;

public abstract class ConfigBasedTest extends APVPluginTest {

	protected Configurator cfg;
	
	public ConfigBasedTest() {
	}
	
	@BeforeEach
	public void setupConfigurator() {
		cfg = new Configurator(parent);
		when(parent.getConfigurator()).thenReturn(cfg);
		
		Answer<APV<? extends APVPlugin>> answer = new Answer<APV<? extends APVPlugin>>() {
	        @SuppressWarnings({ "unchecked", "rawtypes" })
			public APV<? extends APVPlugin> answer(InvocationOnMock invocation) throws Throwable {
	        	SYSTEM_NAMES name = invocation.getArgument(0);
	        	return new APV(parent, name);
	        }
	    };
		
		when(parent.getSystem(Mockito.any())).thenAnswer(answer);
	}

	@Override
	protected void setFrameIndexes() {

	}

}
