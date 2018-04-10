package com.arranger.apv.test;

import static org.mockito.Mockito.when;

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
import com.arranger.apv.Main;
import com.arranger.apv.audio.Audio;
import com.arranger.apv.audio.Audio.BeatInfo;
import com.arranger.apv.util.PeekIterator;

import ddf.minim.analysis.BeatDetect;

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
        
        //mock audio and beat info
        when(parent.getAudio()).thenReturn(audio);
        when(audio.getBeatInfo()).thenReturn(beatInfo);
        assert(parent.getAudio().getBeatInfo() != null);
        
        //mock pulse detector
        when(beatInfo.getPulseDetector()).thenReturn(beatDetect);
        when(beatDetect.isOnset()).thenReturn(true);
        assert(parent.getAudio().getBeatInfo().getPulseDetector() != null);
        
        //mock freq detector
        when(beatInfo.getFreqDetector()).thenReturn(beatDetect);
        when(beatDetect.isRange(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        assert(parent.getAudio().getBeatInfo().getFreqDetector() != null);
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
}
