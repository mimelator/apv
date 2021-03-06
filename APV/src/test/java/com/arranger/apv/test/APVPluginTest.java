package com.arranger.apv.test;

import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.arranger.apv.APV;
import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;
import com.arranger.apv.Main.SYSTEM_NAMES;
import com.arranger.apv.agent.APVAgent;
import com.arranger.apv.audio.Audio;
import com.arranger.apv.audio.BeatInfo;
import com.arranger.apv.cmd.CommandSystem;
import com.arranger.apv.control.ControlSystem.CONTROL_MODES;
import com.arranger.apv.event.APVChangeEvent;
import com.arranger.apv.event.CoreEvent;
import com.arranger.apv.event.DrawShapeEvent;
import com.arranger.apv.event.EventTypes;
import com.arranger.apv.helpers.APVPulseListener;
import com.arranger.apv.helpers.HotKeyHelper;
import com.arranger.apv.helpers.MacroHelper;
import com.arranger.apv.util.APVSetListPlayer;
import com.arranger.apv.util.ColorHelper;
import com.arranger.apv.util.Configurator;
import com.arranger.apv.util.FileHelper;
import com.arranger.apv.util.FileHelper.StreamConsumer;
import com.arranger.apv.util.FontHelper;
import com.arranger.apv.util.ImageHelper;
import com.arranger.apv.util.PeekIterator;
import com.arranger.apv.util.VersionInfo;
import com.arranger.apv.util.cmdrunner.StartupCommandRunner;
import com.arranger.apv.util.draw.RandomMessagePainter;
import com.arranger.apv.wm.APVWatermark;

import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import processing.core.PShape;

public abstract class APVPluginTest {
	
	@Mock
	protected Main parent;
	
	@InjectMocks
	protected APVPlugin apvPlugin;

	protected int frameIndexStart;
	protected int frameIndexEnd;
	protected PeekIterator<Integer> frameIterator;
	protected List<String> filesToRemove = new ArrayList<String>();
	
	protected Configurator cfg;
	
	protected void debug(String msg) {
		System.out.println(msg);
	}
	
	@AfterEach
	void cleanup() {
		FileHelper fh = new FileHelper(parent);
		filesToRemove.forEach(f -> {
			File file = new File(fh.getFullPath(f));
			if (file.exists()) {
				file.delete();
			}
		});
		filesToRemove.clear();
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
        FFT fft = Mockito.mock(FFT.class);
        CommandSystem commandSystem = Mockito.mock(CommandSystem.class);
        
        //mock audio and beat info
        when(parent.getAudio()).thenReturn(audio);
        when(audio.getBeatInfo()).thenReturn(beatInfo);
        when(beatInfo.getFFT()).thenReturn(fft);
        assert(parent.getAudio().getBeatInfo() != null);
        
        when(beatInfo.getPulseDetector()).thenReturn(beatDetect);
        when(beatDetect.isOnset()).thenReturn(true);
        assert(parent.getAudio().getBeatInfo().getPulseDetector() != null);

        when(beatInfo.getFreqDetector()).thenReturn(beatDetect);
        when(beatDetect.isRange(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        assert(parent.getAudio().getBeatInfo().getFreqDetector() != null);
        
        when(parent.getPulseListener()).thenReturn(Mockito.mock(APVPulseListener.class));
        when(parent.getWatermark()).thenReturn(Mockito.mock(APVWatermark.class));
        when(parent.getColorHelper()).thenReturn(new ColorHelper(parent));
        when(parent.getImageHelper()).thenReturn(new ImageHelper(parent));
        when(parent.getCommandSystem()).thenReturn(commandSystem);
        when(parent.getConfig()).thenCallRealMethod();
        when(parent.createShape()).thenReturn(new PShape());
        when(parent.getCurrentControlMode()).thenReturn(CONTROL_MODES.MANUAL);
        when(parent.format(Mockito.any())).thenCallRealMethod();
        when(parent.format(Mockito.any(), Mockito.anyBoolean())).thenCallRealMethod();
        when(parent.getConfigValueForFlag(Mockito.any())).thenCallRealMethod();
        when(parent.getConfigValueForFlag(Mockito.any(), Mockito.any())).thenCallRealMethod();
        when(parent.getConfigBoolean(Mockito.anyString())).thenReturn(false);
        when(parent.getSetupEvent()).thenReturn(new CoreEvent(parent, EventTypes.SETUP));
        when(parent.getSceneCompleteEvent()).thenReturn(new CoreEvent(parent, EventTypes.SCENE_COMPLETE));
        when(parent.getDrawEvent()).thenReturn(new CoreEvent(parent, EventTypes.DRAW));
        when(parent.getSparkEvent()).thenReturn(new DrawShapeEvent(parent, EventTypes.SPARK));
        when(parent.getCarnivalEvent()).thenReturn(new DrawShapeEvent(parent, EventTypes.CARNIVAL));
        when(parent.getStrobeEvent()).thenReturn(new CoreEvent(parent, EventTypes.STROBE));
        when(parent.getAPVChangeEvent()).thenReturn(new APVChangeEvent(parent));
        when(parent.getSetListPlayer()).thenReturn(new APVSetListPlayer(parent));
        when(parent.getVersionInfo()).thenReturn(Mockito.mock(VersionInfo.class));
        when(parent.getFontHelper()).thenReturn(Mockito.mock(FontHelper.class));
        when(parent.getImageHelper()).thenReturn(Mockito.mock(ImageHelper.class));
        when(parent.getRandomMessagePainter()).thenReturn(Mockito.mock(RandomMessagePainter.class));
        when(parent.getStartupCommandRunner()).thenReturn(Mockito.mock(StartupCommandRunner.class));
        when(parent.getAgent()).thenReturn(Mockito.mock(APVAgent.class));
        when(parent.getHotKeyHelper()).thenReturn(Mockito.mock(HotKeyHelper.class));
        when(parent.getMacroHelper()).thenReturn(Mockito.mock(MacroHelper.class));
        
        setupConfigurator();
    }
    
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
    
	protected void getResourceAsStream(String resource, StreamConsumer handler) {
		new FileHelper(parent).getResourceAsStream(resource, handler);
	}
}
