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

import ddf.minim.analysis.BeatDetect;

public abstract class APVPluginTest {
	
	@Mock
	protected Main parent;
	
	@InjectMocks
	protected APVPlugin apvPlugin;

	protected int frameIndexStart;
	protected int frameIndexEnd;
	protected PeekIterator<Integer> frameIterator;
	
	
    @BeforeAll
    static void beforeAll() {
        //System.out.println("Before all test methods");
    }
 
    @BeforeEach
    void beforeEach() {
    	System.out.println("APVPluginTest#beforeEach");
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
        
        when(parent.getAudio()).thenReturn(audio);
        when(audio.getBeatInfo()).thenReturn(beatInfo);
        when(beatInfo.getPulseDetector()).thenReturn(beatDetect);
        when(beatDetect.isOnset()).thenReturn(true);
        assert(parent.getAudio().getBeatInfo().getPulseDetector() != null);
    }

	@AfterEach
    void afterEach() {
        //System.out.println("After each test method");
    }
 
    @AfterAll
    static void afterAll() {
        //System.out.println("After all test methods");
    }
    
    protected abstract void setFrameIndexes();
    
	/**
	 * increments the Frame Iterator
	 */
	protected void advanceFrame() {
		frameIterator.next();
	}
    
    protected List<Integer> createFrameData(int startFrame, int endFrame) {
    	return IntStream.rangeClosed(startFrame, endFrame).boxed().collect(Collectors.toList());
    }
}
