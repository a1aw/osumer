package com.github.mob41.osumer.updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.mob41.osumer.Config;
import com.github.mob41.osumer.exceptions.DebuggableException;
import com.github.mob41.osumer.io.Downloader;
import com.github.mob41.osumer.io.Osu;

public class Updater {
	
	public static final int UPDATE_SOURCE_STABLE = 0;
	
	public static final int UPDATE_SOURCE_BETA = 1;
	
	public static final int UPDATE_SOURCE_SNAPSHOT = 2;
	
	public static final int DEFAULT_UPDATE_SOURCE = UPDATE_SOURCE_SNAPSHOT; //Only applies on snapshot branch

	//This checksum is to prevent if my account is hacked and the updater
	//executable is changed, the updater won't run.
	private static final String LEGACY_UPDATER_MD5_CHECKSUM = "";
	
	private static final String VERSION_LIST = "https://github.com/mob41/osumer-updater/releases/download/legacy/versions.json";
	
	private static final String UPDATER_JAR = "https://github.com/mob41/osumer-updater/releases/download/legacy/updater.jar";
	
	private static final String SOURCE_SNAPSHOT = "snapshot";
	
	private static final String SOURCE_BETA = "beta";
	
	private static final String SOURCE_STABLE = "stable";
	
	public static final int CHECKSUM_FAILED = -3;
	
	public static final int NO_DATA = -2;
	
	public static final int CONN_FAILED = -1;
	
	public static final int NO_UPDATE = 0;
	
	public static final int UPDATE_NEEDED = 1;
	
	public static final int TOO_OLD = 2;
	
	private Downloader dwn = null;

	private Config config;
	
	public Updater(Config config) {
		this.config = config;
	}
	
	public String getThisVersion(){
		return Osu.OSUMER_VERSION;
	}
	
	public VersionInfo getLatestVersion() throws DebuggableException{
		final String thisVersion = Osu.OSUMER_VERSION;
		final String buildBranch = Osu.OSUMER_BRANCH;
		final int buildNum = Osu.OSUMER_BUILD_NUM;
		final int updateSource = config.getUpdateSource();
		
		URL url = null;
		
		try {
			url = new URL(VERSION_LIST);
		} catch (MalformedURLException e){
			throw new DebuggableException(
					VERSION_LIST,
					"URL url = null;",
					"new URL(VERSION_LIST);",
					"URLConnection conn = url.openConnection();",
					"",
					false, e);
		}
		
		String data = "";
		try {
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			
			InputStream in = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			String line;
			while ((line = reader.readLine()) != null){
				data += line;
			}
		} catch (IOException e) {
			throw new DebuggableException(
					VERSION_LIST,
					"URL url = new URL(VERSION_LIST);",
					"(lots of code) -- Connecting and fetch data",
					"Data validating (isEmpty / null)",
					"",
					false, e);
		}
		
		if (data == null || data.isEmpty()){
			throw new DebuggableException(
					VERSION_LIST,
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
			throw new DebuggableException(
					VERSION_LIST,
					"Data validating (isEmpty / null)",
					"Create JSONObject",
					"JSONObject validating \"sources\" parameter",
					"Structure invalid",
					false, e);
		}
		
		if (json.isNull("sources")){
			throw new DebuggableException(
					VERSION_LIST,
					"Create JSONObject",
					"JSONObject validating \"sources\" parameter",
					"Get latest version from JSON",
					"Structure invalid, missing \"sources\" parameter",
					false);
		}
		
		// sources -> snapshot (updateSource) -> 1.0.0 (version) -> (array: index0) 1 (build-number)
		JSONObject sourcesJson = json.getJSONObject("sources");
		JSONArray buildsArr;
		
		String sourceKey = null;
		switch (updateSource){
		case UPDATE_SOURCE_SNAPSHOT:
			sourceKey = SOURCE_SNAPSHOT;
			break;
		case UPDATE_SOURCE_BETA:
			sourceKey = SOURCE_BETA;
			break;
		case UPDATE_SOURCE_STABLE:
			sourceKey = SOURCE_STABLE;
			break;
		default:
			return null;
		}
		
		if (sourcesJson.isNull(sourceKey)){
			return null;
		}
		
		if (sourcesJson.isNull(thisVersion)){
			return null;
		}
		
		buildsArr = sourcesJson.getJSONArray(thisVersion);
		
		if (buildsArr.length() < buildNum){
			return null;
		}
		
		int latest = buildsArr.length();
		
		JSONObject verJson = buildsArr.getJSONObject(latest - 1);
		
		if ((verJson.isNull("last") || !verJson.getBoolean("last")) &&
				(!buildBranch.equals(sourceKey) || latest != buildNum)){
			return new VersionInfo(thisVersion, updateSource, buildNum, false, false);
		}
		
		//As the version is ended, we are finding a new version here
		Iterator<String> it = sourcesJson.keys();
		String key;
		String upgradeNode = null;
		while (it.hasNext()){
			key = it.next();
			switch (compareVersion(thisVersion, key)){
			case -2:
				break;
			case -1:
				upgradeNode = key;
				break;
			case 0:
				break;
			case 1:
				break;
			}
		}
		
		int upgradedBuildNum = upgradeNode != null ?
				sourcesJson.getJSONArray(upgradeNode).length() : -1;
		
		return upgradeNode != null && upgradedBuildNum != -1 ?
				new VersionInfo(upgradeNode, updateSource, buildNum, false, true) :
				new VersionInfo(thisVersion, updateSource, buildNum, true, false);
	}
	
	public boolean isUpdateAvailable() throws DebuggableException{
		VersionInfo latestVer = getLatestVersion();
		return latestVer != null && !latestVer.isThisVersion();
	}
	
	//TODO: Implement updater
	public int getUpdate(VersionInfo verInfo){
		return NO_DATA;
	}
	
	/**
	 *  Returns 1 if ver0 newer than ver1<br>
		Returns -1 if ver0 older than ver1<br>
		Returns -2 if ver0 or ver1 are not a versionNode<br>
		Returns 0 if they are the same<br>
	 */
	public static int compareVersion(String ver0, String ver1){
		int[] ver0sub = separateVersion(ver0);
		int[] ver1sub = separateVersion(ver1);
		
		if (ver0sub == null || ver1sub == null){
			return -2;
		}
		
		return compareVersion(ver0sub[0], ver0sub[1], ver0sub[2], ver1sub[0], ver1sub[1], ver1sub[2]);
	}
	
	/**
	 *  Returns 1 if ver0 newer than ver1<br>
		Returns -1 if ver0 older than ver1<br>
		Returns 0 if they are the same<br>
	 */
	public static int compareVersion(int ver0sub0, int ver0sub1, int ver0sub2, int ver1sub0, int ver1sub1, int ver1sub2){
		if (ver0sub0 > ver1sub0){
			return 1;
		} else if (ver0sub0 < ver1sub0){
			return -1;
		}
		
		if (ver0sub1 > ver1sub1){
			return 1;
		} else if (ver0sub1 < ver1sub1){
			return -1;
		}
		
		if (ver0sub2 > ver1sub2){
			return 1;
		} else if (ver0sub2 < ver1sub2){
			return -1;
		}
		
		return 0;
	}
	
	public static int[] separateVersion(String versionNode){
		int[] out = new int[3];
		
		String str = versionNode;
		int outIndex = 0;
		int tmp = 0;
		int dotIndex = -1;
		for (int i = 0; i < versionNode.length(); i++){
			dotIndex = str.indexOf('.');
			
			if (dotIndex == -1){
				if (outIndex != 2){
					return null;
				}
			} else if (outIndex >= 3){
				return out;
			}
			
			str = str.substring(tmp, dotIndex);
			
			try {
				out[outIndex] = Integer.parseInt(str);
			} catch (NumberFormatException e){
				return null;
			}
			outIndex++;
		}
		
		return out;
	}

}
