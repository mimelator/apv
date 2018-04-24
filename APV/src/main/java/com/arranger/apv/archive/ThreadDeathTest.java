package com.arranger.apv.archive;

import java.lang.Thread.UncaughtExceptionHandler;

import com.arranger.apv.test.APVPluginTest;

public class ThreadDeathTest extends APVPluginTest {

	public ThreadDeathTest() {
	}

	//@Test
	public void testThreadGropu() throws Exception {
		
		UncaughtExceptionHandler eh = new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.out.println("uncaughtException!!!!!!!!");
				System.out.println("uncaughtException: " + e);
				System.out.println("uncaughtException!!!!!!!!");
			}
		};
		
		System.out.println("Before setting");
		checkEH();
		
		Thread.currentThread().setUncaughtExceptionHandler(eh);
		Thread.setDefaultUncaughtExceptionHandler(eh);
		
		System.out.println("After setting");
		checkEH();
		
		throw new Error();
	}

	protected void checkEH() {
		UncaughtExceptionHandler defEH = Thread.currentThread().getUncaughtExceptionHandler();
		System.out.println(defEH);
	}
	
	
	@Override
	protected void setFrameIndexes() {

	}

}
