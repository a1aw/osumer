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
package com.github.mob41.osumer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.mob41.osumer.io.beatmap.Osu;
import com.github.mob41.osumer.io.queue.QueueManager;
import com.github.mob41.osumer.updater.Updater;

public class Config {

	public static final String DEFAULT_DATA_FILE_NAME = "osumer_configuration.json";
	
	private JSONObject json;
	
	private final String dataFilePath;
	
	private final String dataFileName;
	
	public Config(String dataFilePath, String dataFileName) {
		this.dataFilePath = dataFilePath;
		this.dataFileName = dataFileName;
	}
	
	public String getDefaultBrowser(){
		if (json.isNull("defaultBrowser")){
			return null;
		}
		return json.getString("defaultBrowser");
	}
	
	public JSONObject getJson(){
		return json;
	}
	
	public void removeDefaultBrowser(){
		json.remove("defaultBrowser");
	}
	
	public void removeUser(){
		json.remove("user");
	}
	
	public void removePass(){
		json.remove("pass");
	}
	
	public void setThisVersionGettingStartedOnStartupEnabled(boolean enabled){
	    json.put("getting_started_startup_" + Osu.OSUMER_VERSION + "-" + Osu.OSUMER_BRANCH + "-" + Osu.OSUMER_BUILD_NUM, enabled);
	}
	
	public void setUpdateSource(int updateSource){
		json.put("updateSource", updateSource);
	}
	
	public void setSwitchToBrowserIfWithoutUiArg(boolean switchTo){
		json.put("switchToBrowserIfWithoutUiArg", switchTo);
	}
	
	public void setDefaultBrowser(String path){
		json.put("defaultBrowser", path);
	}
	
	public void setAutoSwitchBrowser(boolean autoSwitch){
		json.put("autoSwitchBrowser", autoSwitch);
	}
	
	public void setOEEnabled(boolean enabled){
		json.put("oeenabled", enabled);
	}
	
	public void setUser(String username){
		try {
			json.put("user", Base64.encodeBase64String(username.getBytes("UTF-8")));
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void setPass(String password){
		try {
			json.put("pass", Base64.encodeBase64String(password.getBytes("UTF-8")));
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean isThisVersionGettingStartedOnStartupEnabled(){
	    if (json.isNull("getting_started_startup_" + Osu.OSUMER_VERSION + "-" + Osu.OSUMER_BRANCH + "-" + Osu.OSUMER_BUILD_NUM)){
	        return true;
	    }
	    
	    return false;
	}
	
	public boolean isSwitchToBrowserIfWithoutUiArg(){
		if (json.isNull("switchToBrowserIfWithoutUiArg")){
			return false;
		}
		
		return json.getBoolean("switchToBrowserIfWithoutUiArg");
	}
	
	public boolean isAutoSwitchBrowser(){
		if (json.isNull("autoSwitchBrowser")){
			return true;
		}
		
		return json.getBoolean("autoSwitchBrowser");
	}
	
	public boolean isOEEnabled(){
		if (json.isNull("oeenabled")){
			return true;
		}
		
		return json.getBoolean("oeenabled");
	}
	
	public String getUser(){
		if (json.isNull("user")){
			return null;
		}
		
		String str = json.getString("user");
		try {
			return new String(Base64.decodeBase64(str), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getPass(){
		if (json.isNull("pass")){
			return null;
		}
		
		String str = json.getString("pass");
		try {
			return new String(Base64.decodeBase64(str), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getUpdateSource(){
		if (json.isNull("updateSource")){
			return Updater.DEFAULT_UPDATE_SOURCE;
		}
		
		return json.getInt("updateSource");
	}

	public void load() throws IOException{
		File file = new File(dataFilePath + "/" + dataFileName);
		if (!file.exists()){
			write();
			return;
		}
		FileInputStream in = new FileInputStream(file);
		
		String data = "";
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		while((line = reader.readLine()) != null){
			data += line;
		}
		reader.close();
		
		/*
		Preferences prefs = Preferences.userRoot();
		
		String data = prefs.get("com.github.mob41.osumer.configuration", null);
		
		if (data == null){
			write();
			return;
		}
		*/
		
		try {
			json = new JSONObject(data);
		} catch (JSONException e){
			throw new IOException("Invalid configuration data structure", e);
		}
	}
	
	public void write() throws IOException{
		
		File folder = new File(dataFilePath);
		if (!folder.exists()){
			folder.mkdirs();
		}
		
		File file = new File(dataFilePath + "/" + dataFileName);
		if (!file.exists()){
			file.createNewFile();
			json = new JSONObject();
		}
		FileOutputStream out = new FileOutputStream(file);
		PrintWriter writer = new PrintWriter(out, true);
		writer.println(json.toString(5));
		writer.close();
		out.flush();
		out.close();
		
		/*
		Preferences prefs = Preferences.userRoot();
		
		if (json == null){
			json = new JSONObject();
		}
		
		prefs.put("com.github.mob41.osumer.configuration", json.toString());
		
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			throw new IOException("Error occurred", e);
		}
		*/
	}

    public void setMaxThreads(int maxThreads) {
        json.put("maxThreads", maxThreads);
    }

    public int getMaxThreads() {
        if (json.isNull("maxThreads")){
            return QueueManager.DEFAULT_MAX_THREADS;
        }
        
        return json.getInt("maxThreads");
    }

}
