package com.github.mob41.osumer.exceptions;

public class NoSuchVersionException extends DebuggableException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4508589028039107867L;
	private static final String MSG = "No such version are defined in the source JSON";
	
	public NoSuchVersionException(String json, String last_op, String this_op, String next_op, String source, String version) {
		super(json, last_op, this_op, next_op, MSG + ": " + source + "/" + version, false);
	}

	public NoSuchVersionException(String json, String last_op, String this_op, String next_op, String arg0, String source, String version) {
		super(json, last_op, this_op, next_op, MSG + ": " + source + "/" + version, false, arg0);
	}

	public NoSuchVersionException(String json, String last_op, String this_op, String next_op, Throwable arg0, String source, String version) {
		super(json, last_op, this_op, next_op, MSG + ": " + source + "/" + version, false, arg0);
	}

	public NoSuchVersionException(String json, String last_op, String this_op, String next_op, String arg0, Throwable arg1, String source, String version) {
		super(json, last_op, this_op, next_op, MSG + ": " + source + "/" + version, false, arg0, arg1);
	}

	public NoSuchVersionException(String json, String last_op, String this_op, String next_op, String arg0, Throwable arg1, boolean arg2, boolean arg3, String source, String version) {
		super(json, last_op, this_op, next_op, MSG + ": " + source + "/" + version, false,  arg0, arg1, arg2, arg3);
	}

}
