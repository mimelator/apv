package com.arranger.apv.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class FrameCountTest extends APVPluginTest {
	
	private static final int DEFAULT_FRAME_INDEX_START = 3;
	private static final int DEFAULT_FRAME_INDEX_END = 5;
	
	@BeforeEach
    void beforeEach() {
		super.beforeEach();
	}

    @Test
    public void frameCountTest() throws Throwable {
    	debug("FrameCountTest#frameCountTest");
    	assert(apvPlugin != null);
    	assert(apvPlugin.getParent() != null);
        assert(apvPlugin.getParent().getFrameCount() == 3);
        assert(apvPlugin.getParent().getFrameCount() == 3);
        
        frameIterator.next();
        assert(apvPlugin.getParent().getFrameCount() == 4);
        
        frameIterator.next();
        assert(apvPlugin.getParent().getFrameCount() == 5);
    }
    
    @Override
    protected void setFrameIndexes() {
    	frameIndexStart = DEFAULT_FRAME_INDEX_START;
    	frameIndexEnd = DEFAULT_FRAME_INDEX_END;
    }
}
