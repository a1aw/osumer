package com.github.mob41.osumer.exceptions;

public class OsuException extends RuntimeException {
	
	public OsuException() {
		
	}

	public OsuException(String arg0) {
		super(arg0);
	}

	public OsuException(Throwable arg0) {
		super(arg0);
	}

	public OsuException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public OsuException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
