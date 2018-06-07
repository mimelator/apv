package com.arranger.apv.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import com.arranger.apv.APVPlugin;
import com.arranger.apv.Main;

/**
 * @see https://crunchify.com/how-to-generate-java-thread-dump-programmatically/
 */
public class RuntimeHelper extends APVPlugin {

	public RuntimeHelper(Main parent) {
		super(parent);
	}
	
	public String generateMemoryDump() {
		StringBuilder dump = new StringBuilder();
		Runtime rt = Runtime.getRuntime();
		
		dump.append("total memory: ").append(rt.totalMemory()).append(System.lineSeparator());
		dump.append("free memory: ").append(rt.freeMemory()).append(System.lineSeparator());
		
		return dump.toString();
	}

	public String generateThreadDump() {
		final StringBuilder dump = new StringBuilder();
		final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		final ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 100);
		for (ThreadInfo threadInfo : threadInfos) {
			dump.append('"');
			dump.append(threadInfo.getThreadName());
			dump.append("\" ");
			final Thread.State state = threadInfo.getThreadState();
			dump.append("\n   java.lang.Thread.State: ");
			dump.append(state);
			final StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();
			for (final StackTraceElement stackTraceElement : stackTraceElements) {
				dump.append("\n        at ");
				dump.append(stackTraceElement);
			}
			dump.append("\n\n");
		}
		return dump.toString();
	}
	
}
