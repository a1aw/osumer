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

public class Config {

	public static final String DEFAULT_DATA_FILE_NAME = "osumer_configuration.json";
	
	private JSONObject json;
	
	private final String dataFilePath;
	
	public Config(String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}
	
	public String getDefaultBrowserPath(){
		if (json.isNull("defaultBrowserPath")){
			return null;
		}
		return json.getString("defaultBrowserPath");
	}
	
	public JSONObject getJson(){
		return json;
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
		File file = new File(dataFilePath);
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
		
		try {
			json = new JSONObject(data);
		} catch (JSONException e){
			throw new IOException("Invalid configuration data structure", e);
		}
	}
	
	public void write() throws IOException{
		File file = new File(dataFilePath);
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
	}

}
