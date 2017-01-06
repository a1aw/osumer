package com.github.mob41.osumer.exceptions;

public class DebuggableException extends OsuException {
	
	private final DebugDump dump;
	
	public DebuggableException(String specified_data, String last_op, String this_op, String next_op, String message, boolean containsPrivateData) {
		dump = new DebugDump(specified_data, last_op, this_op, next_op, message, containsPrivateData, this);
	}

	public DebuggableException(String specified_data, String last_op, String this_op, String next_op, String message, boolean containsPrivateData, String arg0) {
		super(arg0);
		dump = new DebugDump(specified_data, last_op, this_op, next_op, message, containsPrivateData, this);
	}

	public DebuggableException(String specified_data, String last_op, String this_op, String next_op, String message, boolean containsPrivateData, Throwable arg0) {
		super(arg0);
		dump = new DebugDump(specified_data, last_op, this_op, next_op, message, containsPrivateData, this);
	}

	public DebuggableException(String specified_data, String last_op, String this_op, String next_op, String message, boolean containsPrivateData, String arg0, Throwable arg1) {
		super(arg0, arg1);
		dump = new DebugDump(specified_data, last_op, this_op, next_op, message, containsPrivateData, this);
	}

	public DebuggableException(String specified_data, String last_op, String this_op, String next_op, String message, boolean containsPrivateData, String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		dump = new DebugDump(specified_data, last_op, this_op, next_op, message, containsPrivateData, this);
	}
	
	public DebugDump getDump(){
		return dump;
	}

}
