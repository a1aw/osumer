package com.github.mob41.osumer.daemon;

import com.github.mob41.osumer.Configuration;
import com.github.mob41.osumer.OsumerNative;

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
				OsumerNative.injectOverlay();
			}
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
}
