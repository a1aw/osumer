package com.github.mob41.osumer.updater;

import java.util.Calendar;

public class Announcement {
	
	private final Calendar time;
	
	private final String text;
	
	public Announcement(Calendar time, String text) {
		this.time = time;
		this.text = text;
	}

	public Calendar getTime() {
		return time;
	}

	public String getText() {
		return text;
	}

}
