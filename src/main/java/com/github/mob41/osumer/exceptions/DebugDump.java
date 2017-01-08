package com.github.mob41.osumer.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.Calendar;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import com.github.mob41.osumer.io.Osu;
import com.github.mob41.osumer.ui.ErrorDumpDialog;

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
	
	private final String lastoperation;
	
	private final String thisoperation;
	
	private final String nextoperation;
	
	protected DebugDump(String specified_data, String last_op, String this_op, String next_op, boolean containsPrivateData, String message){
		this(specified_data, last_op, this_op, next_op, message, containsPrivateData, null);
	}
	
	protected DebugDump(String specified_data, String last_op, String this_op, String next_op, String message, boolean containsPrivateData, Exception e) {
		os = System.getProperty("os.name");
		osumerVersion = Osu.OSUMER_VERSION;
		debuggerVersion = "unknown";
		
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
		} else {
			stacktrace = null;
		}
		
		this.message = message;
		
		this.containsPrivateData = containsPrivateData;
		this.specified_data = specified_data;
		this.lastoperation = last_op;
		this.thisoperation = this_op;
		this.nextoperation = next_op;
		
		DumpManager.getInstance().addDump(this);
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
	
	public static void showDebugDialog(DebuggableException e){
		ErrorDumpDialog dialog = new ErrorDumpDialog(e);
		dialog.setVisible(true);
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

}
