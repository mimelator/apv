package com.arranger.apv.archive;

import java.lang.Thread.UncaughtExceptionHandler;

public class UehTest {
	private UncaughtExceptionHandler eh = new UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			System.out.println("uncaughtException!!!!!!!!");
			System.out.println("uncaughtException: " + e);
			System.out.println("uncaughtException!!!!!!!!");
		}
	};
	
	private UehTest() {
		//install
		Thread.currentThread().setUncaughtExceptionHandler(eh);
		Thread.setDefaultUncaughtExceptionHandler(eh);
	}
	
	public void test() {
		throw new Error("Will anyone catch this?");
	}
}
