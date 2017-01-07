package com.github.mob41.osumer.exceptions;

public class NoBuildsForVersionException extends DebuggableException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4508589028039107867L;
	private static final String MSG = "No builds are defined in the version JSON.";
	
	public NoBuildsForVersionException(String json, String last_op, String this_op, String next_op) {
		super(json, last_op, this_op, next_op, MSG, false);
	}

	public NoBuildsForVersionException(String json, String last_op, String this_op, String next_op, String arg0) {
		super(json, last_op, this_op, next_op, MSG, false, arg0);
	}

	public NoBuildsForVersionException(String json, String last_op, String this_op, String next_op, Throwable arg0) {
		super(json, last_op, this_op, next_op, MSG, false, arg0);
	}

	public NoBuildsForVersionException(String json, String last_op, String this_op, String next_op, String arg0, Throwable arg1) {
		super(json, last_op, this_op, next_op, MSG, false, arg0, arg1);
	}

	public NoBuildsForVersionException(String json, String last_op, String this_op, String next_op, String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(json, last_op, this_op, next_op, MSG, false,  arg0, arg1, arg2, arg3);
	}

}
