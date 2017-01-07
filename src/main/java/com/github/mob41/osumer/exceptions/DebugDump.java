package com.github.mob41.osumer.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

import org.json.JSONObject;

import com.github.mob41.osumer.io.Osu;

public class DebugDump {
	
	private final String os;
	
	private final String osumerVersion;
	
	private final String debuggerVersion;
	
	private final String message;
	
	private final long generated;
	
	private final boolean containsPrivateData;
	
	private final String generated_human;
	
	private final String stacktrace;
	
	private final String specified_data;
	
	private final String lastoperation;
	
	private final String thisoperation;
	
	private final String nextoperation;
	
	public DebugDump(String specified_data, String last_op, String this_op, String next_op, boolean containsPrivateData, String message){
		this(specified_data, last_op, this_op, next_op, message, containsPrivateData, null);
	}
	
	public DebugDump(String specified_data, String last_op, String this_op, String next_op, String message, boolean containsPrivateData, Exception e) {
		os = System.getProperty("os.name");
		osumerVersion = Osu.OSUMER_VERSION;
		debuggerVersion = "unknown";
		
		Calendar cal = Calendar.getInstance();
		
		generated = cal.getTimeInMillis();
		generated_human = cal.getTime().toString();
		
		if (e != null){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.close();
			
			stacktrace = sw.toString();	
		} else {
			stacktrace = null;
		}
		
		this.message = message;
		
		this.containsPrivateData = containsPrivateData;
		this.specified_data = specified_data;
		this.lastoperation = last_op;
		this.thisoperation = this_op;
		this.nextoperation = next_op;
	}
	
	@Override
	public String toString(){
		return 	"===!===!===!===!-osumer-debugger-dump-!===!===!===!===\n" +
				"Debug information generated on " + generated_human + " (" + generated + " ms)\n" +
				"osumerDebugger v." + debuggerVersion + "\n" +
				"osumer v." + osumerVersion + "\n" +
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
	
	public static void showDebugDialog(DebugDump dump){
		
	}

}
