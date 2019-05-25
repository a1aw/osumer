package com.github.mob41.osumer.method;

import java.io.Serializable;

import com.github.mob41.osumer.debug.DebugDump;

public class MethodResult<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5828470383744187539L;

	private T result;
	
	private DebugDump dump;
	
	public MethodResult(T result) {
		this(result, null);
	}
	
	public MethodResult(T result, DebugDump dump) {
		this.result = result;
		this.dump = dump;
	}
	
	public T getResult() {
		return result;
	}
	
	public DebugDump getDump() {
		return dump;
	}
	
}
