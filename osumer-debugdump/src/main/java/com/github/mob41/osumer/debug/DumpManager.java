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
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

public class DumpManager {
	
	private static MetricRegistry metrics;

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
		
		metrics = new MetricRegistry();
		
		final Graphite graphite = new Graphite(new InetSocketAddress("graphite.osumer.ml", 2003));
		final GraphiteReporter reporter = GraphiteReporter.forRegistry(metrics)
		                                                  .prefixedWith("osumer.mob41.github.com")
		                                                  .convertRatesTo(TimeUnit.SECONDS)
		                                                  .convertDurationsTo(TimeUnit.MILLISECONDS)
		                                                  .filter(MetricFilter.ALL)
		                                                  .build(graphite);
		reporter.start(1, TimeUnit.MINUTES);
		
		metrics.meter("debugManagerInit").mark();
		
		readDumps();
		init = true;
	}
	
	public static MetricRegistry getMetrics() {
		if (!init) {
			throw new IllegalStateException("DumpManager was not initialized before getting metrics registry!");
		}
		return metrics;
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
		if (!init) {
			throw new IllegalStateException("DumpManager was not initialized before adding this dump!");
		}
		try {
			writeDump(dump);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("DumpManager: Error writing dump");
		}
	}
	
	public static void writeDump(DebugDump dump) throws IOException {
		if (!init) {
			throw new IllegalStateException("DumpManager was not initialized before writing this dump!");
		}
		
		String name = dump.getExceptionClass();
		
		if (name == null) {
			name = "Typed-Dump";
		}
		
		metrics.meter(MetricRegistry.name("exceptions", name)).mark();
		
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
