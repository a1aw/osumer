/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Anthony Law
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package com.github.mob41.osumer.exceptions;

import java.awt.GraphicsEnvironment;

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
