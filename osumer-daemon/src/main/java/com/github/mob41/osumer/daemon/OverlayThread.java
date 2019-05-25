package com.github.mob41.osumer.daemon;

import com.github.mob41.osumer.Configuration;
import com.github.mob41.osumer.OsumerNative;
import com.github.mob41.osumer.debug.DebugDump;
import com.github.mob41.osumer.debug.DumpManager;

public class OverlayThread extends Thread{

	private final Configuration config;
	
	public OverlayThread(Configuration config) {
		super();
		this.config = config;
	}

	@Override
	public void run() {
		while (!this.isInterrupted()) {
			if (config.isOverlayEnabled()) {
				try {
					OsumerNative.injectOverlay();
				} catch (Throwable e) {
					DumpManager.addDump(new DebugDump(null, "Check is overlay enabled in configuration", "Ask native DLL to look for osu! for injection", "Sleep for 2500 ms", "Error calling DLL", false, e));
					break;
				}
			}
			
			try {
				Thread.sleep(2500);
			} catch (InterruptedException ignore) {
				break;
			}
		}
	}
	
}
