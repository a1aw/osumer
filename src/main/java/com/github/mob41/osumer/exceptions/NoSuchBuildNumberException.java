package com.github.mob41.osumer.exceptions;

public class NoSuchBuildNumberException extends DebuggableException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4508589028039107867L;
	private static final String MSG = "No such build number/index defined in the versions' JSON";
	
	public NoSuchBuildNumberException(String json, String last_op, String this_op, String next_op, int buildnum) {
		super(json, last_op, this_op, next_op, MSG + ": " + buildnum, false);
	}

	public NoSuchBuildNumberException(String json, String last_op, String this_op, String next_op, String arg0, int buildnum) {
		super(json, last_op, this_op, next_op, MSG + ": " + buildnum, false, arg0);
	}

	public NoSuchBuildNumberException(String json, String last_op, String this_op, String next_op, Throwable arg0, int buildnum) {
		super(json, last_op, this_op, next_op, MSG + ": " + buildnum, false, arg0);
	}

	public NoSuchBuildNumberException(String json, String last_op, String this_op, String next_op, String arg0, Throwable arg1, int buildnum) {
		super(json, last_op, this_op, next_op, MSG + ": " + buildnum, false, arg0, arg1);
	}

	public NoSuchBuildNumberException(String json, String last_op, String this_op, String next_op, String arg0, Throwable arg1, boolean arg2, boolean arg3,int buildnum) {
		super(json, last_op, this_op, next_op, MSG + ": " + buildnum, false,  arg0, arg1, arg2, arg3);
	}

}
