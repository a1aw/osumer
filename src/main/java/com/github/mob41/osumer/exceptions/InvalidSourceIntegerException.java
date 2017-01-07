package com.github.mob41.osumer.exceptions;

public class InvalidSourceIntegerException extends DebuggableException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4508589028039107867L;
	private static final String MSG = "Invalid source integer provided in the configuration";
	
	public InvalidSourceIntegerException(String json, String last_op, String this_op, String next_op, int source) {
		super(json, last_op, this_op, next_op, MSG + ": " + source, false);
	}

	public InvalidSourceIntegerException(String json, String last_op, String this_op, String next_op, String arg0, int source) {
		super(json, last_op, this_op, next_op, MSG + ": " + source, false, arg0);
	}

	public InvalidSourceIntegerException(String json, String last_op, String this_op, String next_op, Throwable arg0, int source) {
		super(json, last_op, this_op, next_op, MSG + ": " + source, false, arg0);
	}

	public InvalidSourceIntegerException(String json, String last_op, String this_op, String next_op, String arg0, Throwable arg1, int source) {
		super(json, last_op, this_op, next_op, MSG + ": " + source, false, arg0, arg1);
	}

	public InvalidSourceIntegerException(String json, String last_op, String this_op, String next_op, String arg0, Throwable arg1, boolean arg2, boolean arg3, int source) {
		super(json, last_op, this_op, next_op, MSG + ": " + source, false,  arg0, arg1, arg2, arg3);
	}

}
