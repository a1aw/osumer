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
package com.github.mob41.osumer.debug;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.Calendar;

import org.apache.commons.codec.binary.Hex;

public class DebugDump {
	
	private final String os;
	
	private final String osumerVersion;
	
	private final String debuggerVersion;
	
	private final String message;
	
	private final String uid;
	
	private final long generated;
	
	private final boolean containsPrivateData;
	
	private final String generated_human;
	
	private final String stacktrace;
	
	private final String specified_data;
	
	private final String exceptionClass;
	
	private final String lastoperation;
	
	private final String thisoperation;
	
	private final String nextoperation;
	
	public DebugDump(String specified_data, String last_op, String this_op, String next_op, boolean containsPrivateData, String message){
		this(specified_data, last_op, this_op, next_op, message, containsPrivateData, null);
	}
	
	public DebugDump(String specified_data, String last_op, String this_op, String next_op, String message, boolean containsPrivateData, Throwable e) {
		os = System.getProperty("os.name");
		osumerVersion = DumpManager.getOsumerVersion();
		debuggerVersion = DumpManager.getDebuggerVersion();
		
		Calendar cal = Calendar.getInstance();
		
		byte[] randuid = new byte[128/8];
		new SecureRandom().nextBytes(randuid);
		uid = Hex.encodeHexString(randuid);
		
		generated = cal.getTimeInMillis();
		generated_human = cal.getTime().toString();
		
		if (e != null){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.close();
			
			stacktrace = sw.toString();	
			exceptionClass = e.getClass().getCanonicalName();
		} else {
			stacktrace = null;
			exceptionClass = null;
		}
		
		this.message = message;
		
		this.containsPrivateData = containsPrivateData;
		this.specified_data = specified_data;
		this.lastoperation = last_op;
		this.thisoperation = this_op;
		this.nextoperation = next_op;
		
		try {
			DumpManager.writeDump(this);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.err.println("DumpManager: Error writing dump");
		}
	}
	
	public String getOs(){
		return os;
	}
	
	public long getGenerated(){
		return generated;
	}
	
	@Override
	public String toString(){
		return 	"===!===!===!===!-osumer-debugger-dump-!===!===!===!===\n" +
				"Debug information generated on " + generated_human + " (" + generated + " ms)\n" +
				"osumerDebugger v." + debuggerVersion + "\n" +
				"osumer v." + osumerVersion + "\n" +
				"\n" +
				"O/S Name: " + os + "\n" +
				"\n" +
				"Current operation: " + thisoperation + "\n" +
				"Returned message: " + message + "\n" +
				"\n" +
				"Last operation: " + lastoperation + "\n" +
				"Next operation: " + nextoperation + "\n" +
				"\n" +
				"Data specified: " + (containsPrivateData ? "(Contains private data)" : specified_data) + "\n" +
				"\n" +
				"Exception stacktrace: " + (stacktrace == null ? "(Does not contain exception)" : "\n" + stacktrace) + "\n" +
				"===!===!===!===!-dump-end-!===!===!===!===\n";
	}

	public String getOsumerVersion() {
		return osumerVersion;
	}

	public String getDebuggerVersion() {
		return debuggerVersion;
	}

	public String getMessage() {
		return message;
	}

	public String getUid() {
		return uid;
	}

	public boolean isContainsPrivateData() {
		return containsPrivateData;
	}

	public String getGeneratedHuman() {
		return generated_human;
	}

	public String getStacktrace() {
		return stacktrace;
	}

	public String getSpecifiedData() {
		return specified_data;
	}

	public String getLastOperation() {
		return lastoperation;
	}

	public String getThisOperation() {
		return thisoperation;
	}

	public String getNextOperation() {
		return nextoperation;
	}

	public String getExceptionClass() {
		return exceptionClass;
	}

}
