package com.github.mob41.osumer.io;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.mob41.osumer.exceptions.DebuggableException;

public class Osu {
	
//TODO: Hard-code version?
	
	public static final String OSUMER_VERSION = "0.1.3";
	
	public static final String OSUMER_BRANCH = "snapshot";
	
	public static final int OSUMER_BUILD_NUM = 1;
	
	public static final int SUCCESS = 0;
	
	public static final int INVALID_USERNAME_PASSWORD = 1;
	
	private static final String LOGOUT_URL = "http://osu.ppy.sh/forum/ucp.php?mode=logout";
	
	private static final String LOGIN_URL = "http://osu.ppy.sh/forum/ucp.php?mode=login";
	
	private static final String INDEX_LOCATION_URL = "http://osu.ppy.sh/forum/index.php";
	
	public static final String URL_PREFIX = "http://osu.ppy.sh/";

	public static final String URL_PREFIX_SSL = "https://osu.ppy.sh/";
	
	public static final String BEATMAP_DIR = "b/";
	
	public static final String SONG_DIR = "s/";
	
	private final CookieManager cmgr;
	
	public Osu() {
		cmgr = new CookieManager();
	}
	
	public static boolean isWindows(){
		return System.getProperty("os.name").contains("Windows");
	}
	
	public static boolean isWindowsElevated(){
		if (!isWindows()){
			return false;
		}
		
		final String programfiles = System.getenv("PROGRAMFILES");
		
		if (programfiles == null || programfiles.length() < 1) {
			throw new IllegalStateException("OS mismatch. Program Files directory not detected");
		}
		
		File testPriv = new File(programfiles);
		if (!testPriv.canWrite()) {
			return false;
		}
		File fileTest = null;
		
		try {
			fileTest = File.createTempFile("testsu", ".dll", testPriv);
		} catch (IOException e) {
			return false;
		} finally {
			if (fileTest != null) {
				fileTest.delete();
			}
		}
		return true;
	}
	
	public OsuBeatmap getBeatmapInfo(String beatmapLink) throws DebuggableException{
		try {
			URL url = new URL(beatmapLink);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			if (cmgr.getCookieStore().getCookies().size() > 0) {
			    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
			    conn.setRequestProperty("Cookie",
			    join(";", cmgr.getCookieStore().getCookies()));    
			}
			
			conn.setUseCaches(false);
			conn.setDoOutput(false);
			conn.setDoInput(true);
			conn.setAllowUserInteraction(false);
			conn.setRequestMethod("GET");
			
			//Fake environment from Chrome
			conn.setRequestProperty("Connection", "Keep-alive");
			conn.setRequestProperty("Cache-Control", "max-age=0");
			conn.setRequestProperty("Origin", "https://osu.ppy.sh");
			conn.setRequestProperty("Upgrade-Insecure-Requests", "0");
			conn.setRequestProperty("User-Agent", "User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
			conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			conn.setRequestProperty("DNT", "1");
			//conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
			conn.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.8,en;q=0.6");
			
			String data = "";
			String line;
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			
			while((line = reader.readLine()) != null){
				data += line;
			}
			
			Map<String, List<String>> headerFields = conn.getHeaderFields();
			
			List<String> cookiesHeader = headerFields.get("Set-Cookie");

			if (cookiesHeader != null) {
			    for (String cookie : cookiesHeader) {
			        cmgr.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
			    }               
			}
			
			Document doc = Jsoup.parse(data);
			return OsuBeatmap.createInstance(doc);
			/*
			Elements elements = doc.getElementsByClass("beatmap_download_link");
			
			if (elements.size() == 0){
				throw new DebuggableException("No download link available. Invalid beatmap url?");
			}
			
			Element alnk = elements.get(0);
			
			String href = alnk.attr("href");
			
			return href;
			*/
		} catch (Exception e) {
			throw new DebuggableException(
					beatmapLink,
					"(Try&catch try) getting beatmap info",
					"Throw debuggable exception on catch",
					"(End of function)",
					"Error occurred when getting beatmap info",
					false, e);
		}
	}
	
	public int login(String username, String password) throws DebuggableException{
		try {
			String urlPara = 
					"login=Login&" +
					"username=" + username + "&" + //Username
					"password=" + password + "&" + //Password
					"autologin=" + true + "&" +   //Log me on automatically each visit
					"viewonline=" + true + "&" +   //Hide my online this session
					"redirect=" + "index.php" + "&" + //Redirect (To check whether the login is success)
					"sid="; //Session ID, not distributed once
			
			byte[] postData = urlPara.getBytes(StandardCharsets.UTF_8);
			int postLen = postData.length;
			
			URL url = new URL(LOGIN_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("POST");
			conn.setInstanceFollowRedirects(false);
			
			if (cmgr.getCookieStore().getCookies().size() > 0) {
			    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
			    conn.setRequestProperty("Cookie",
			    join(";", cmgr.getCookieStore().getCookies()));    
			}
			
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			
			//Fake environment from Chrome
			conn.setRequestProperty("Connection", "Keep-alive");
			conn.setRequestProperty("Cache-Control", "max-age=0");
			conn.setRequestProperty("Origin", "https://osu.ppy.sh");
			conn.setRequestProperty("Upgrade-Insecure-Requests", "0");
			conn.setRequestProperty("User-Agent", "User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
			conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			conn.setRequestProperty("DNT", "1");
			conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
			conn.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.8,en;q=0.6");
			
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(postLen));
			
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(urlPara);
			wr.close();
			
			String data = "";
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			while((line = reader.readLine()) != null){
				data += line + "\n";
			}
			
			Map<String, List<String>> headerFields = conn.getHeaderFields();
			
			List<String> locationHeader = headerFields.get("Location");
			
			if (locationHeader == null || locationHeader.size() != 1 || !locationHeader.get(0).equals(INDEX_LOCATION_URL)){
				throw new DebuggableException(
						"",
						"Get \"Location\" from headerFields and assign to locationHeader",
						"Validate locationHeader is not null or locationHeader.size() is 1 or locationHeader.get(0) is equal to INDEX_LOCATION_URL",
						"Get \"Set-Cookie\" from headerFields and assign to cookiesHeader",
						"Login failed. Redirected to a non-index page.",
						true);
			}
			
			List<String> cookiesHeader = headerFields.get("Set-Cookie");

			if (cookiesHeader != null) {
			    for (String cookie : cookiesHeader) {
			        cmgr.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
			    }               
			}
			
			return SUCCESS;
		} catch (Exception e) {
			throw new DebuggableException(
					"",
					"(Try&catch try) Logging in",
					"Throw debuggable exception on catch",
					"(End of function)",
					"Error occurred when logging in",
					true, e);
		}
	}
	
	public int logout(String sid) throws DebuggableException{
		try {
			String urlPara =
					"sid=" + sid; //Session ID
			
			URL url = new URL(LOGOUT_URL + "?" + urlPara);
			URLConnection conn = url.openConnection();
			
			if (cmgr.getCookieStore().getCookies().size() > 0) {
			    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
			    conn.setRequestProperty("Cookie",
			    join(";", cmgr.getCookieStore().getCookies()));    
			}
			
			conn.setUseCaches(false);
			
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", "0");
			
			Map<String, List<String>> headerFields = conn.getHeaderFields();
			
			List<String> cookiesHeader = headerFields.get("Set-Cookie");

			if (cookiesHeader != null) {
			    for (String cookie : cookiesHeader) {
			        cmgr.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
			    }               
			}
			
			String data = "";
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			while((line = reader.readLine()) != null){
				data += line;
			}
			
			System.out.println(data);
			return SUCCESS;
		} catch (Exception e) {
			throw new DebuggableException(
					"",
					"(Try&catch try) Logging out",
					"Throw debuggable exception on catch",
					"(End of function)",
					"Error occurred when logging out",
					true, e);
		}
	}
	
	public static boolean isVaildBeatMapUrl(String urlstr){
		return (urlstr.length() > URL_PREFIX.length() + 2 &&
				(urlstr.substring(0, URL_PREFIX.length() + 2).equals(URL_PREFIX  + BEATMAP_DIR) ||
						urlstr.substring(0, URL_PREFIX.length() + 2).equals(URL_PREFIX  + SONG_DIR))) ||
				(urlstr.length() > URL_PREFIX_SSL.length() + 2 &&
						(urlstr.substring(0, URL_PREFIX_SSL.length() + 2).equals(URL_PREFIX_SSL  + BEATMAP_DIR) ||
								urlstr.substring(0, URL_PREFIX_SSL.length() + 2).equals(URL_PREFIX_SSL  + SONG_DIR)));
	}
	
	protected CookieManager getCookies(){
		return cmgr;
	}
	
	private static void printAllHeaders(Map<String, List<String>> headers){
		Iterator<String> it = headers.keySet().iterator();
		List<String> strs;
		String key;
		while (it.hasNext()){
			key = it.next();
			strs = headers.get(key);
			
			for (int i = 0; i < strs.size(); i++){
				System.out.println(key + " (" + i + "):" + strs.get(i));
			}
		}
		
	}
	
	private static String join(String separator, List<HttpCookie> objs){
		String out = "";
		
		String str;
		for (int i = 0; i < objs.size(); i++){
			str = objs.get(i).toString();
			
			out += str + separator;
			
			if (i != objs.size() - 1){
				out += " ";
			}
		}
		
		return out;
	}

}
