package com.github.mob41.osumer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

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
	
	public void setSwitchToBrowserIfWithoutUiArg(boolean switchTo){
		json.put("switchToBrowserIfWithoutUiArg", switchTo);
	}
	
	public void setDefaultBrowser(String path){
		json.put("defaultBrowser", path);
	}
	
	public void setAutoSwitchBrowser(boolean autoSwitch){
		json.put("autoSwitchBrowser", autoSwitch);
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

}
