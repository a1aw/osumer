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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DumpManager {
	
	private static String osumerVersion;
	
	private static String debuggerVersion;
	
	private static Map<Long, String> dumps;
	
	private static boolean init = false;
	
	public static void init(String osumerVersion, String debuggerVersion) throws IOException {
		if (init) {
			return;
		}
		DumpManager.osumerVersion = osumerVersion;
		DumpManager.debuggerVersion = debuggerVersion;
		readDumps();
		init = true;
	}

	public static String getDebuggerVersion() {
		return debuggerVersion;
	}

	public static void setDebuggerVersion(String debuggerVersion) {
		DumpManager.debuggerVersion = debuggerVersion;
	}

	public static String getOsumerVersion() {
		return osumerVersion;
	}

	public static void setOsumerVersion(String osumerVersion) {
		DumpManager.osumerVersion = osumerVersion;
	}
	
	public static Map<Long, String> getDumps(){
		return dumps;
	}
	
	public static boolean readDumps() throws IOException{
        String path = System.getenv("localappdata") + "\\osumerExpress\\dumps";
        File folder = new File(path);
        
        if (!folder.exists()) {
        	return false;
        }
        
        dumps = new HashMap<Long, String>();
        
        long time;
        String name;
        int extDot = -1;
        File[] files = folder.listFiles();
        for (File file : files) {
        	name = file.getName();
        	extDot = name.lastIndexOf('.');
        	if (extDot != -1) {
        		name = name.substring(0, extDot);
        	}
        	try {
        		time = Long.parseLong(name);
        	} catch (NumberFormatException e) {
        		System.err.println("DumpManager: Could not load \"" + file.getName() + "\" as dump. Not a timestamp.");
        		continue;
        	}
        	
        	try {
        		FileInputStream out = new FileInputStream(file);
        		BufferedReader reader = new BufferedReader(new InputStreamReader(out));
        		String line;
        		String data = "";
        		while ((line = reader.readLine()) != null) {
        			data += line;
        		}
        		reader.close();
        		out.close();
            	
            	dumps.put(time, data);
        	} catch (IOException e) {
        		e.printStackTrace();
        		System.err.println("DumpManager: Error reading old dumps at " + time + " ms");
        		continue;
        	}
        }
        
        return true;
	}
	
	public static void addDump(DebugDump dump) {
		try {
			writeDump(dump);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("DumpManager: Error writing dump");
		}
	}
	
	public static void writeDump(DebugDump dump) throws IOException {
        String path = System.getenv("localappdata") + "\\osumerExpress\\dumps";
        File folder = new File(path);
        folder.mkdirs();
        
        File file = new File(path + "\\" + dump.getGenerated() + ".txt");
        FileOutputStream out = new FileOutputStream(file);
        PrintWriter writer = new PrintWriter(out, true);
        writer.println(dump.toString());
        writer.flush();
        writer.close();
        out.close();
	}

}
