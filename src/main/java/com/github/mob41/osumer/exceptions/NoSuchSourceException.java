package com.github.mob41.osumer.exceptions;

public class NoSuchSourceException extends DebuggableException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4508589028039107867L;
	private static final String MSG = "No such source are defined in the update JSON";
	
	public NoSuchSourceException(String json, String last_op, String this_op, String next_op, String source) {
		super(json, last_op, this_op, next_op, MSG + ": " + source, false);
	}

	public NoSuchSourceException(String json, String last_op, String this_op, String next_op, String arg0, String source) {
		super(json, last_op, this_op, next_op, MSG + ": " + source, false, arg0);
	}

	public NoSuchSourceException(String json, String last_op, String this_op, String next_op, Throwable arg0, String source) {
		super(json, last_op, this_op, next_op, MSG + ": " + source, false, arg0);
	}

	public NoSuchSourceException(String json, String last_op, String this_op, String next_op, String arg0, Throwable arg1, String source) {
		super(json, last_op, this_op, next_op, MSG + ": " + source, false, arg0, arg1);
	}

	public NoSuchSourceException(String json, String last_op, String this_op, String next_op, String arg0, Throwable arg1, boolean arg2, boolean arg3, String source) {
		super(json, last_op, this_op, next_op, MSG + ": " + source, false,  arg0, arg1, arg2, arg3);
	}

}
