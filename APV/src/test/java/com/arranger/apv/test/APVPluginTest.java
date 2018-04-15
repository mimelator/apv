package com.arranger.apv.test;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.CommandSystem;
import com.arranger.apv.Main;
import com.arranger.apv.ControlSystem.CONTROL_MODES;
import com.arranger.apv.audio.Audio;
import com.arranger.apv.audio.Audio.BeatInfo;
import com.arranger.apv.util.APVPulseListener;
import com.arranger.apv.util.PeekIterator;

import ddf.minim.analysis.BeatDetect;
import processing.core.PShape;

public abstract class APVPluginTest {
	
	@Mock
	protected Main parent;
	
	@InjectMocks
	protected APVPlugin apvPlugin;

	protected int frameIndexStart;
	protected int frameIndexEnd;
	protected PeekIterator<Integer> frameIterator;
	
	/**
	 * Not using java.util.logging for tests just yet
	 */
	protected void debug(String msg) {
		System.out.println(msg);
	}
	
	
    @BeforeAll
    static void beforeAll() {
        //debug("Before all test methods");
    }
 
    @BeforeEach
    void beforeEach() {
    	debug("APVPluginTest#beforeEach");
        MockitoAnnotations.initMocks(this);
        setFrameIndexes();
        frameIterator = new PeekIterator<Integer>(createFrameData(frameIndexStart, frameIndexEnd).iterator());
        when(parent.getFrameCount()).thenAnswer(new Answer<Integer>() {
        	public Integer answer(InvocationOnMock invocation) throws Throwable {
        		return frameIterator.peek();
        	}
		});
        
        Audio audio = Mockito.mock(Audio.class);
        BeatInfo beatInfo = Mockito.mock(BeatInfo.class);
        BeatDetect beatDetect = Mockito.mock(BeatDetect.class);
        APVPulseListener apvPulseListener = Mockito.mock(APVPulseListener.class);
        CommandSystem commandSystem = Mockito.mock(CommandSystem.class);
        
        //mock audio and beat info
        when(parent.getAudio()).thenReturn(audio);
        when(audio.getBeatInfo()).thenReturn(beatInfo);
        assert(parent.getAudio().getBeatInfo() != null);
        
        //mock audio's pulse detector and more
        when(beatInfo.getPulseDetector()).thenReturn(beatDetect);
        when(beatDetect.isOnset()).thenReturn(true);
        assert(parent.getAudio().getBeatInfo().getPulseDetector() != null);
        
        //mock audio's freq detector
        when(beatInfo.getFreqDetector()).thenReturn(beatDetect);
        when(beatDetect.isRange(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        assert(parent.getAudio().getBeatInfo().getFreqDetector() != null);
        
        //mock Main's default pulse listener and command listener
        when(parent.getPulseListener()).thenReturn(apvPulseListener);
        when(parent.getCommandSystem()).thenReturn(commandSystem);
        when(parent.getConfig()).thenCallRealMethod();
        when(parent.createShape()).thenReturn(new PShape());
        when(parent.getCurrentControlMode()).thenReturn(CONTROL_MODES.MANUAL);
    }

	@AfterEach
    void afterEach() {
        //debug("After each test method");
    }
 
    @AfterAll
    static void afterAll() {
        //debug("After all test methods");
    }
    
    protected abstract void setFrameIndexes();
    
	/**
	 * increments the Frame Iterator
	 */
	protected void advanceFrame() {
		debug("Advancing frame: " + parent.getFrameCount());
		frameIterator.next();
	}
    
    protected List<Integer> createFrameData(int startFrame, int endFrame) {
    	return IntStream.rangeClosed(startFrame, endFrame).boxed().collect(Collectors.toList());
    }
    
	@FunctionalInterface
	public static interface StreamConsumer {
		public void consumeInputStream(InputStream is) throws Exception;
	}
	
	
	protected void getInputStream(String resource, StreamConsumer handler) {
		InputStream inputStream = null;
		try {
			debug("Loading resource: " + resource);
			inputStream = getClass().getClassLoader().getResourceAsStream(resource);
		    assert(inputStream != null);
		    handler.consumeInputStream(inputStream);
		} catch (Exception ex) {
		    System.out.println(ex.getMessage());
		    ex.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				    e.printStackTrace();
				}
			}
		}
	}
}
