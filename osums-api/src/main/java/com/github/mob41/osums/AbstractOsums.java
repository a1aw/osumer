package com.github.mob41.osums;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.jsoup.nodes.Document;

import com.github.mob41.osumer.debug.WithDumpException;
import com.github.mob41.osums.beatmap.OsuBeatmap;
import com.github.mob41.osums.beatmap.OsuSong;
import com.github.mob41.osums.search.SongResult;
import com.github.mob41.osums.search.SearchFilter;
import com.github.mob41.osums.search.SearchResult;

public abstract class AbstractOsums {
	
	public static final String RANK_ANY = "any";
	
	public static final String RANK_RANKED = "ranked";
	
	public static final String RANK_PENDING = "pending";
	
	public static final String RANK_QUALIFIED = "qualified";
	
	public static final String RANK_LOVED = "loved";

    public static final int SUCCESS = 0;

    public static final int ALREADY_LOGGED_IN = 1;

    public static final int INVALID_USERNAME_PASSWORD = -1;

    public static final int ERROR = -2;

    private final CookieManager cookieMgr;
    
    public AbstractOsums() {
    	this.cookieMgr = new CookieManager();
    }
    
    public final CookieManager getCookieManager() {
    	return cookieMgr;
    }
    
    public final void setCookies(URLConnection conn) {
        if (cookieMgr.getCookieStore().getCookies().size() > 0) {
            // While joining the Cookies, use ',' or ';' as needed. Most of
            // the servers are using ';'
            conn.setRequestProperty("Cookie", join(";", cookieMgr.getCookieStore().getCookies()));
        }
    }
    
    public final void retrieveCookies(Map<String, List<String>> headerFields) {
    	List<String> cookiesHeader = new ArrayList<String>();
        Iterator<String> it = headerFields.keySet().iterator();
        while (it.hasNext()) {
        	String key = it.next();
            if (key != null && key.toLowerCase().equals("set-cookie")) {
            	cookiesHeader.addAll(headerFields.get(key));
            }
        }

        if (cookiesHeader.size() > 0) {
            for (String cookie : cookiesHeader) {
            	cookieMgr.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
        }
    }
    
    protected final HttpURLConnection prepareUrlConnectionGet(URL url) throws IOException {
    	HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        setCookies(conn);

        conn.setUseCaches(false);
        conn.setDoOutput(false);
        conn.setDoInput(true);
        conn.setAllowUserInteraction(false);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("GET");

        fakeEnvHeaders(conn);
        
        return conn;
    }
    
    protected final HttpURLConnection prepareUrlConnectionPost(URL url) throws IOException {
    	HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        setCookies(conn);

        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setAllowUserInteraction(false);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        
        fakeEnvHeaders(conn);
        
        return conn;
    }
    
    protected final void fakeEnvHeaders(URLConnection conn) {
        // Fake environment from Chrome
        conn.setRequestProperty("Connection", "Keep-alive");
        conn.setRequestProperty("Cache-Control", "max-age=0");
        conn.setRequestProperty("Origin", "https://osu.ppy.sh");
        conn.setRequestProperty("Upgrade-Insecure-Requests", "0");
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
        conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        conn.setRequestProperty("DNT", "1");
        conn.setRequestProperty("Accept-Encoding", "gzip"); //Accept gzip encoding
        conn.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.8,en;q=0.6");
    }
    
    protected final String readAllData(InputStream in) throws IOException {
    	String data = "";
        String line;
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        while ((line = reader.readLine()) != null) {
            data += line;
        }
        
        reader.close();
        
        return data;
    }
    
    protected final String getHttpCookiedContent(String link) throws IOException {
    	URL url = new URL(link);

        HttpURLConnection conn = prepareUrlConnectionGet(url);
        
        InputStream in = prepareInputStream(conn);
        String data = readAllData(in);
        
        Map<String, List<String>> headerFields = conn.getHeaderFields();

        retrieveCookies(headerFields);
        
        return data;
    }
    
    protected final InputStream prepareInputStream(URLConnection conn) throws IOException {
    	String encoding = conn.getHeaderField("Content-Encoding");
        InputStream in;
        if (encoding != null && encoding.equals("gzip")){
            in = new GZIPInputStream(conn.getInputStream());
        } else {
            in = conn.getInputStream();
        }
        return in;
    }

    private final static String join(String separator, List<HttpCookie> objs) {
        String out = "";

        String str;
        for (int i = 0; i < objs.size(); i++) {
            str = objs.get(i).toString();

            out += str + separator;

            if (i != objs.size() - 1) {
                out += " ";
            }
        }

        return out;
    }
    
    public final SearchResult searchOsumsMaps(String keywords) throws WithDumpException {
    	return searchOsumsMaps(keywords, null, 1);
    }
    
    public final SearchResult searchOsumsMaps(String keywords, SearchFilter[] filters) throws WithDumpException {
    	return searchOsumsMaps(keywords, filters, 1);
    }
    
    public final SearchResult searchOsumsMaps(String keywords, SearchFilter[] filters, int page) throws WithDumpException {
    	return null;
    }
    
    public SearchResult searchOnlineMaps(String keywords) throws WithDumpException {
    	return searchOsumsMaps(keywords, null, 1);
    }
    
    public SearchResult searchOnlineMaps(String keywords, SearchFilter[] filters) throws WithDumpException {
    	return searchOsumsMaps(keywords, filters, 1);
    }
    
    public abstract boolean isVaildBeatmapUrl(String url);
    
    public abstract SearchResult searchOnlineMaps(String keywords, SearchFilter[] filters, int page) throws WithDumpException;
    
    public abstract int login(String username, String password) throws WithDumpException;
    
    public abstract boolean logout() throws WithDumpException;
    
    public abstract boolean isLoggedIn();
    
    public abstract boolean checkLoggedIn() throws IOException;
    
    public abstract OsuSong getSongInfo(String link) throws WithDumpException;
    
    public abstract OsuBeatmap getBeatmapInfo(String link) throws WithDumpException;
	
	public abstract SongResult[] parseSearch(String category, Document doc) throws WithDumpException;
	
	public abstract OsuSong parseSong(String originalUrl, Document doc) throws WithDumpException;
    
}
