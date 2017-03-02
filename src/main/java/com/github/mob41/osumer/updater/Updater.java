package com.github.mob41.osumer.updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.mob41.osumer.Config;
import com.github.mob41.osumer.exceptions.DebuggableException;
import com.github.mob41.osumer.exceptions.InvalidSourceIntegerException;
import com.github.mob41.osumer.exceptions.NoBuildsForVersionException;
import com.github.mob41.osumer.exceptions.NoSuchBuildNumberException;
import com.github.mob41.osumer.exceptions.NoSuchSourceException;
import com.github.mob41.osumer.exceptions.NoSuchVersionException;
import com.github.mob41.osumer.io.OsuDownloader;
import com.github.mob41.osumer.io.Osu;

public class Updater {
	
	public static final int UPDATE_SOURCE_STABLE = 0;
	
	public static final int UPDATE_SOURCE_BETA = 1;
	
	public static final int UPDATE_SOURCE_SNAPSHOT = 2;
	
	public static final int DEFAULT_UPDATE_SOURCE = UPDATE_SOURCE_SNAPSHOT; //Only applies on snapshot branch

	//This checksum is to prevent if my account is hacked and the updater
	//executable is changed, the updater won't run.
	private static final String LEGACY_UPDATER_MD5_CHECKSUM = "";
	
	private static final String VERSION_LIST = "https://mob41.github.io/osumer-updater/versions.json";
	
	public static final String LEGACY_UPDATER_JAR = "https://github.com/mob41/osumer-updater/releases/download/legacy/osumer-updater.exe";
	
	private static final String SOURCE_SNAPSHOT = "snapshot";
	
	private static final String SOURCE_BETA = "beta";
	
	private static final String SOURCE_STABLE = "stable";
	
	public static final int CHECKSUM_FAILED = -3;
	
	public static final int NO_DATA = -2;
	
	public static final int CONN_FAILED = -1;
	
	public static final int NO_UPDATE = 0;
	
	public static final int UPDATE_NEEDED = 1;
	
	public static final int TOO_OLD = 2;
	
	private OsuDownloader dwn = null;

	private Config config;
	
	public Updater(Config config) {
		this.config = config;
	}
	
	public String getThisVersion(){
		return Osu.OSUMER_VERSION;
	}
	
	public UpdateInfo getLatestVersion() throws DebuggableException{
		final String thisVersion = Osu.OSUMER_VERSION;
		final String buildBranch = Osu.OSUMER_BRANCH;
		final int buildNum = Osu.OSUMER_BUILD_NUM;
		final int updateSource = config.getUpdateSource();
		
		URL url = null;
		
		try {
			url = new URL(VERSION_LIST + "?update=" + Calendar.getInstance().getTimeInMillis());
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
					"Convert source integer to string",
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
			throw new InvalidSourceIntegerException(
					json.toString(5),
					"JSONObject validating \"sources\" parameter",
					"Convert source integer to string",
					"Validate sources' JSONObject and set variable",
					updateSource);
		}
		
		if (sourcesJson.isNull(sourceKey)){
			throw new NoSuchSourceException(
					json.toString(5),
					"Convert source integer to string",
					"Validate sources' JSONObject and set variable",
					"Validate versions' JSONObject and set variable",
					sourceKey);
		}
		
		JSONObject versionsJson = sourcesJson.getJSONObject(sourceKey);
		
		if (versionsJson.isNull(thisVersion)){
			throw new NoSuchVersionException(
					json.toString(5),
					"Validate sources' JSONObject and set variable",
					"Validate versions' JSONObject and set variable",
					"Validate builds JSONArray and set variable",
					sourceKey,
					thisVersion);
		}
		
		buildsArr = versionsJson.getJSONArray(thisVersion);
		
		if (buildsArr.length() < buildNum){
			throw new NoSuchBuildNumberException(
					json.toString(5),
					"Validate versions' JSONObject and set variable",
					"Validate builds JSONArray and set variable",
					"Get latest build number from JSONArray",
					buildNum);
		}
		
		int latest = buildsArr.length();
		
		JSONObject verJson = buildsArr.getJSONObject(latest - 1);
		
		if ((verJson.isNull("ended") || !verJson.getBoolean("ended")) &&
				(!buildBranch.equals(sourceKey) || latest != buildNum)){
			String webLink = verJson.isNull("web_link") ? null : verJson.getString("web_link");
			String exeLink = verJson.isNull("exe_link") ? null : verJson.getString("exe_link");
			String jarLink = verJson.isNull("jar_link") ? null : verJson.getString("jar_link");
			String desc = verJson.isNull("desc") ? null : verJson.getString("desc");
			return new UpdateInfo(desc, thisVersion, updateSource, latest, webLink, exeLink, jarLink, false, false);
		}
		
		//As the version is ended, we are finding a new version here
		Iterator<String> it = versionsJson.keys();
		String key;
		String upgradeNode = null;
		while (it.hasNext()){
			key = it.next();
			switch (compareVersion(thisVersion, key)){
			case -2:
				break;
			case -1:
				if ((upgradeNode != null && compareVersion(upgradeNode, key) == -1) ||
						upgradeNode == null){
					upgradeNode = key;
				}
				break;
			case 0:
				break;
			case 1:
				break;
			}
		}
		
		JSONArray upgradeNodeArr = null;
		int upgradedBuildNum = -1;
		
		if (upgradeNode != null){
			upgradeNodeArr = versionsJson.getJSONArray(upgradeNode);
			upgradedBuildNum = upgradeNodeArr.length();
		}
		
		if (upgradedBuildNum == 0){
			throw new NoBuildsForVersionException(
					json.toString(5),
					"Get Upgrade Node",
					"Validate build info JSON",
					"Return VersionInfo");
		}
		
		if (upgradeNode != null && upgradedBuildNum != -1){
			JSONObject upgradedVerJson = upgradeNodeArr.getJSONObject(upgradedBuildNum - 1);
			
			String webLink = upgradedVerJson.isNull("web_link") ? null : upgradedVerJson.getString("web_link");
			String exeLink = upgradedVerJson.isNull("exe_link") ? null : upgradedVerJson.getString("exe_link");
			String jarLink = upgradedVerJson.isNull("jar_link") ? null : upgradedVerJson.getString("jar_link");

			String desc = verJson.isNull("desc") ? null : verJson.getString("desc");
			return new UpdateInfo(desc, upgradeNode, updateSource, upgradedBuildNum, webLink, exeLink, jarLink, false, true);
		} else {
			String webLink = verJson.isNull("web_link") ? null : verJson.getString("web_link");
			String exeLink = verJson.isNull("exe_link") ? null : verJson.getString("exe_link");
			String jarLink = verJson.isNull("jar_link") ? null : verJson.getString("jar_link");

			String desc = verJson.isNull("desc") ? null : verJson.getString("desc");
			return new UpdateInfo(desc, thisVersion, updateSource, buildNum, webLink, exeLink, jarLink, true, false);
		}
	}
	
	public boolean isUpdateAvailable() throws DebuggableException{
		UpdateInfo latestVer = getLatestVersion();
		return latestVer != null && !latestVer.isThisVersion();
	}
	
	public static String getUpdaterLink() throws DebuggableException{
		URL url = null;
		
		try {
			url = new URL(VERSION_LIST + "?update=" + Calendar.getInstance().getTimeInMillis());
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
					"JSON stuff",
					"Structure invalid",
					false, e);
		}
		
		return json.isNull("updater_exe_link") ? null : json.getString("updater_exe_link");
	}
	
	//TODO: Implement updater
	public int getUpdate(UpdateInfo verInfo){
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
	
	public static String getBranchStr(int updateSource){
		switch (updateSource){
		case UPDATE_SOURCE_SNAPSHOT:
			return SOURCE_SNAPSHOT;
		case UPDATE_SOURCE_BETA:
			return SOURCE_BETA;
		case UPDATE_SOURCE_STABLE:
			return SOURCE_STABLE;
		default:
			return null;
		}
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
		
		String str;
		int outIndex = 0;
		int tmp = 0;
		for (int i = 0; i <= versionNode.length(); i++){
			if (outIndex >= 3){
				return null;
			}
			
			if (versionNode.length() == i || versionNode.charAt(i) == '.'){
				str = versionNode.substring(tmp, i);
				tmp = i + 1;
				
				try {
					out[outIndex] = Integer.parseInt(str);
				} catch (NumberFormatException e){
					return null;
				}
				
				outIndex++;
			}
		}
		return out;
	}

}
