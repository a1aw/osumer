package com.github.mob41.osumer.updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.mob41.osumer.debug.WithDumpException;

public class AnnouncementChecker {
	
    private static final String ANNOUNCEMENT_LIST = "https://mob41.github.io/osumer-updater/announcements.json";
    
    public Announcement[] getAnnouncements() throws WithDumpException{
        URL url = null;
        
        try {
            url = new URL(ANNOUNCEMENT_LIST + "?update=" + Calendar.getInstance().getTimeInMillis());
        } catch (MalformedURLException e){
            throw new WithDumpException(
            		ANNOUNCEMENT_LIST,
                    "URL url = null;",
                    "new URL(VERSION_LIST);",
                    "URLConnection conn = url.openConnection();",
                    "",
                    false, e);
        }
        
        String data = "";
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Connection", "close");
            conn.setUseCaches(false);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            
            String line;
            while ((line = reader.readLine()) != null){
                data += line;
            }
        } catch (IOException e) {
            throw new WithDumpException(
            		ANNOUNCEMENT_LIST,
                    "URL url = new URL(VERSION_LIST);",
                    "(lots of code) -- Connecting and fetch data",
                    "Data validating (isEmpty / null)",
                    "",
                    false, e);
        }
        
        if (data == null || data.isEmpty()){
            throw new WithDumpException(
            		ANNOUNCEMENT_LIST,
                    "(lots of code) -- Connecting and fetch data",
                    "Data validating (isEmpty / null)",
                    "Create JSONObject",
                    "No data fetched. \"data\" is null/isEmpty",
                    false);
        }
        
        JSONObject json = null;
        try {
            json = new JSONObject(data);
        } catch (JSONException e){
            throw new WithDumpException(
            		ANNOUNCEMENT_LIST,
                    "Data validating (isEmpty / null)",
                    "Create JSONObject",
                    "JSONObject validating \"announcements\" parameter",
                    "Structure invalid",
                    false, e);
        }
        
        if (json.isNull("announcements")){
            throw new WithDumpException(
            		ANNOUNCEMENT_LIST,
                    "Create JSONObject",
                    "JSONObject validating \"sources\" parameter",
                    "Parsing announcement json to objects",
                    "Structure invalid, missing \"announcements\" parameter",
                    false);
        }
        
        JSONArray arr = json.getJSONArray("announcements");
        
        Calendar cal;
        JSONObject obj;
        Announcement[] out = new Announcement[arr.length()];
        for (int i = 0; i < out.length; i++) {
        	obj = arr.getJSONObject(i);
        	if (obj.isNull("time") || obj.isNull("text")) {
                throw new WithDumpException(
                		data,
                        "JSONObject validating \"sources\" parameter",
                        "Parsing announcement json to objects",
                        "Output array",
                        "Structure invalid, announcement json missing some parameters",
                        false);
        	}
        	cal = Calendar.getInstance();
        	cal.setTimeInMillis(obj.getLong("time"));
        	out[i] = new Announcement(cal, obj.getString("text"));
        }
        
        return out;
    }

}
