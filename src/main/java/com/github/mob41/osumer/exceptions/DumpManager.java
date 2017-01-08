package com.github.mob41.osumer.exceptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DumpManager {
	
	private static DumpManager _instance = null;
	
	private List<DebugDump> dumps;

	public static DumpManager getInstance(){
		return _instance != null ? _instance : (_instance = new DumpManager());
	}
	
	public DumpManager() {
		dumps = new ArrayList<DebugDump>();
	}
	
	protected void addDump(DebugDump dump){
		dumps.add(dump);
	}
	
	public boolean removeDump(String uid){
		if (uid == null){
			return false;
		}
		
		DebugDump dump = getDumpByUid(uid);
		
		if (dump == null){
			return false;
		}
		
		return dumps.remove(dump);
	}
	
	public DebugDump[] getDumps(){
		DebugDump[] arr = new DebugDump[dumps.size()];
		for (int i = 0; i < arr.length; i++){
			arr[i] = dumps.get(i);
		}
		return arr;
	}
	
	public DebugDump getDumpByUid(String uid){
		final List<DebugDump> copyList = new ArrayList<DebugDump>(dumps);
		
		if (uid == null){
			return null;
		}
		
		String dumpuid;
		DebugDump dump = null;
		for (int i = 0; i < copyList.size(); i++){
			dump = copyList.get(i);
			dumpuid = dump.getUid();
			if (dumpuid != null && dumpuid.equals(uid)){
				return dump;
			}
		}
		
		return null;
	}
	
	public int getNumberOfDumps(){
		return dumps.size();
	}
	
	public static String combineDumps(DebugDump[] dumps){
		Calendar cal = Calendar.getInstance();
		String out = 
				"Combined dumps generated at " + cal.getTime().toString() + " (" + cal.getTimeInMillis() + " ms)\n" +
				"O/S Name: " + System.getProperty("os.name") + "\n" +
				"Total dumps: " + dumps.length + "\n\n";
		out += "=x=!=x=!=x=!-Start-of-combined-dumps-!=x=!=x=!=x=\n\n";
		DebugDump dump;
		if (dumps == null || dumps.length == 0){
			out += "There are no dumps to be exported.";
		} else {
			for (int i = 0; i < dumps.length; i++){
				out += "dumps[" + i + "]\n";
				dump = dumps[i];
				if (dump == null){
					out += "Dump is null\n";
				} else {
					out += dump.toString();
				}
				out += "\n";
			}
		}
		
		out += "=x=!=x=!=x=!-End-of-combined-dumps-!=x=!=x=!=x=\n";
		return out;
	}

}
