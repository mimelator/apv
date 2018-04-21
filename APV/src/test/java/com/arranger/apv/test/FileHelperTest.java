package com.arranger.apv.test;

import org.junit.jupiter.api.Test;

import com.arranger.apv.util.FileHelper;

public class FileHelperTest extends APVPluginTest {

	private static final String DEFAULT_TEXT = "This is new text";
	private static final String FILE_NAME = "fileHelperTest.txt";
	
	public FileHelperTest() {
	}

	
	@Test
	public void testCreateFile() {
		FileHelper fh = new FileHelper(parent);
		
		boolean b = fh.saveFile(FILE_NAME, DEFAULT_TEXT, false);
		assert(b);
		
		String result = fh.readFile(FILE_NAME);
		assert(DEFAULT_TEXT.equals(result));
		
		fh.saveFile(FILE_NAME, DEFAULT_TEXT, true);
		result = fh.readFile(FILE_NAME);
		assert(DEFAULT_TEXT.length() * 2 == result.length());
	}
	
	
	@Override
	protected void setFrameIndexes() {

	}

}
