/*******************************************************************************
 * Any modification, copies of sections of this file must be attached with this
 * license and shown clearly in the developer's project. The code can be used
 * as long as you state clearly you do not own it. Any violation might result in
 *  a take-down.
 *
 * MIT License
 *
 * Copyright (c) 2016, 2017 Anthony Law
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
package com.github.mob41.osums;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.mob41.osumer.debug.WithDumpException;
import com.github.mob41.osums.beatmap.OsuBeatmap;
import com.github.mob41.osums.index.OnlineIndexManager;
import com.github.mob41.osums.search.ResultBeatmap;
import com.github.mob41.osums.search.SearchingProgressHandler;

public class Osums {

    public static final int SUCCESS = 0;

    public static final int ALREADY_LOGGED_IN = 2;

    public static final int INVALID_USERNAME_PASSWORD = 1;

    private static final String LOGOUT_URL = "http://old.ppy.sh/forum/ucp.php?mode=logout";

    private static final String LOGIN_URL = "http://old.ppy.sh/forum/ucp.php?mode=login";

    private static final String INDEX_LOCATION_URL = "http://old.ppy.sh/forum/index.php";

    private static final String INDEX_LOCATION_NO_INDEXPHP_URL = "http://old.ppy.sh/forum/";

    public static final String URL_PREFIX = "http://osu.ppy.sh/";

    public static final String URL_PREFIX_SSL = "https://osu.ppy.sh/";

    public static final String BEATMAP_DIR = "b/";

    public static final String SONG_DIR = "s/";

    private final CookieManager cmgr;
    
    private final OnlineIndexManager oimgr;

    private boolean loggedIn = false;

    public Osums() {
        cmgr = new CookieManager();
        oimgr = new OnlineIndexManager(System.getenv("LOCALAPPDATA") + "\\osumerExpress", "osums_indexingDatabase.json", this);
        try {
            oimgr.load();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public ResultBeatmap[] searchMaps(String queryString) throws WithDumpException{
        if (oimgr.isIndexed()){
            return oimgr.searchDatabase(queryString);
        } else {
            return null;
        }
    }
    
    public ResultBeatmap[] getLinksOfBeatmapSearch(SearchingProgressHandler handler, String searchLink) throws WithDumpException{
        try {
            handler.onStart();
            List<ResultBeatmap> maps = new ArrayList<ResultBeatmap>();
            
            int totalPages = 1;
            for (int i = 1; i <= totalPages; i++){
                handler.onLoopStart();
                URL url = new URL(searchLink + "&page=" + i);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (cmgr.getCookieStore().getCookies().size() > 0) {
                    // While joining the Cookies, use ',' or ';' as needed. Most of
                    // the servers are using ';'
                    conn.setRequestProperty("Cookie", join(";", cmgr.getCookieStore().getCookies()));
                }

                conn.setUseCaches(false);
                conn.setDoOutput(false);
                conn.setDoInput(true);
                conn.setAllowUserInteraction(false);
                conn.setRequestMethod("GET");

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

                String data = "";
                String line;
                
                String encoding = conn.getHeaderField("Content-Encoding");
                InputStream in;
                if (encoding != null && encoding.equals("gzip")){
                    in = new GZIPInputStream(conn.getInputStream());
                } else {
                    in = conn.getInputStream();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                while ((line = reader.readLine()) != null) {
                    data += line;
                }

                Map<String, List<String>> headerFields = conn.getHeaderFields();

                List<String> cookiesHeader = headerFields.get("Set-Cookie");

                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        cmgr.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    }
                }
                
                reader.close();

                Document doc = Jsoup.parse(data);
                
                Element pagination = doc.getElementsByClass("pagination").first();
                String paginationText = pagination.html();
                
                int brIndex = paginationText.indexOf("<br>");
                if (brIndex == -1){
                    throw new WithDumpException(paginationText, "Get br index in pagination",
                            "Validate brIndex != -1", "Extract pagination text before br",
                            "Could not find at least one br element!", false);
                }
                
                String pageText = paginationText.substring(0, brIndex);
                
                int displayingIndex = pageText.indexOf("Displaying "); //11
                int toIndex = pageText.indexOf(" to "); //4
                int ofIndex = pageText.indexOf(" of "); //4
                int resultIndex = pageText.indexOf(" results."); //9
                
                if (displayingIndex == -1 || toIndex == -1 || ofIndex == -1 || resultIndex == -1){
                    throw new WithDumpException(paginationText, "Get all text indexes",
                            "Validate all text indexes != -1", "Extract result pages data",
                            "The pagination is invalid or unregonized.", false);
                }
                
                //int nowPage = -1;
                int currPageMaps = -1;
                int totalResultMaps = -1;
                try {
                    //nowPage = Integer.parseInt(pageText.substring(displayingIndex + 11, toIndex));
                    String currPageMapsStr = pageText.substring(toIndex + 4, ofIndex);
                    if (!currPageMapsStr.equals("many")){
                        currPageMaps = Integer.parseInt(currPageMapsStr);
                    }
                    
                    String totalResultMapsStr = pageText.substring(ofIndex + 4, resultIndex);
                    if (!totalResultMapsStr.equals("many")){
                        totalResultMaps = Integer.parseInt(totalResultMapsStr);
                    }
                } catch (NumberFormatException e){
                    throw new WithDumpException(pageText, "Validate all text indexes != -1",
                            "Extract result pages data", "Get page data",
                            "Pagination number-text data cannot be decoded as number.", false, e);
                }
                System.out.println("Processing page " + i + " / " + totalPages);
                if (totalResultMaps != -1 && currPageMaps != -1 &&
                        currPageMaps != totalResultMaps){
                    
                    currPageMaps -= 40 * (i - 1);
                    
                    //System.out.println("Using method 1 to identify Total Pages");
                    //System.out.println("TRM/CPM: " + totalResultMaps + " / " + currPageMaps);
                    float calc = ((float) totalResultMaps / currPageMaps);
                    totalPages = (int) calc;
                    
                    if (calc != totalPages){
                        //System.out.println("Calc != totalPages: " + calc + " != " + totalPages);
                        totalPages++;
                    }

                    //System.out.println("Now total pages: " + totalPages);
                    handler.setTotalPages(totalPages);
                } else {
                    //System.out.println("Using method 2 to identify Total Pages");
                    Elements pageLinkEls = pagination.children();
                    
                    int size = pageLinkEls.size();
                    
                    if (size < 2){
                        totalPages = 1;
                        handler.setTotalPages(1);
                        /*
                        throw new WithDumpException(pageText, "Get page links size",
                                "Validate children size >= 2", "Get last page link element",
                                "Invalid page! The page has less than 2 page links!", false);
                        */
                    } else {
                        Element lastPageLinkEl = pageLinkEls.get(size - 2);
                        
                        if (lastPageLinkEl != null){
                            int lastPageNum = -1;
                            try {
                                lastPageNum = Integer.parseInt(lastPageLinkEl.html());
                            } catch (NumberFormatException e){
                                throw new WithDumpException(pageText, "Get last page link element",
                                        "Parse last page number String to number", "Set as total page",
                                        "Pagination last page number-text data cannot be decoded as number.", false, e);
                            }
                            
                            if (lastPageNum > totalPages){
                                System.out.println("Last page num is bigger than total pages: " + lastPageNum + " > " + totalPages);
                                totalPages = lastPageNum;
                            } else {
                                System.out.println("Last page num is sammler than total pages: " + lastPageNum + " < " + totalPages);
                            }
                        }
                        
                        handler.setTotalPages(totalPages);
                    }
                }
                
                Element beatmapsDiv = doc.select("div.beatmapListing").first();
                Iterator<Element> it = beatmapsDiv.children().iterator();
                
                int x = 0;
                while(it.hasNext()){
                    Element el = it.next();
                    
                    Element artistEl = el.select("div.maintext span.artist").first();
                    
                    if (artistEl == null){
                        continue;
                    }
                    
                    String artist = artistEl.html();
                    
                    if (artist.isEmpty()){
                        continue;
                    }
                    
                    Element titleEl = el.select("div.maintext a.title").first();
                    
                    if (titleEl == null){
                        continue;
                    }
                    
                    String title = titleEl.html();
                    
                    if (title.isEmpty()){
                        continue;
                    }
                    
                    Element creatorEl = el.select("div.left-aligned div a").first();
                    
                    if (creatorEl == null){
                        continue;
                    }
                    
                    String creator = creatorEl.html();
                    
                    if (creator.isEmpty()){
                        continue;
                    }
                    
                    int id = -1;
                    try {
                        id = Integer.parseInt(el.attr("id"));
                    } catch (NumberFormatException e){
                        continue;
                    }
                    
                    List<String> tagsList = new ArrayList<String>(30);
                    Element tagsEl = el.select("div.right-aligned div.tags").first();
                    
                    if (tagsEl == null){
                        continue;
                    }
                    
                    Iterator<Element> tagsChildIt = tagsEl.children().iterator();
                    while(tagsChildIt.hasNext()){
                        Element childIt = tagsChildIt.next();
                        
                        tagsList.add(childIt.html());
                    }
                    
                    //TODO: Implement Hearts and Plays readings
                    
                    String[] tags = new String[tagsList.size()];
                    for (int j = 0; j < tags.length; j++){
                        tags[j] = tagsList.get(j);
                    }
                    
                    Element thumbEl = el.select("div.bmlistt").first();
                    String styleStr = thumbEl.attr("style");
                    
                    int startingIndex = styleStr.indexOf("background: url(\""); //17
                    int endingIndex = styleStr.indexOf("\")"); //4
                    
                    if (startingIndex == -1 || endingIndex == -1){
                        System.out.println(styleStr);
                        return null;
                    }
                    
                    String thumbUrl = "https:" + styleStr.substring(startingIndex + 17,  endingIndex);
                    
                    String thumbData = "";
                    
//                    try {
//                        URL tu = new URL(thumbUrl);
//                        
//                        HttpURLConnection tuConn = (HttpURLConnection) tu.openConnection();
//
//                        if (cmgr.getCookieStore().getCookies().size() > 0) {
//                            // While joining the Cookies, use ',' or ';' as needed. Most of
//                            // the servers are using ';'
//                            tuConn.setRequestProperty("Cookie", join(";", cmgr.getCookieStore().getCookies()));
//                        }
//                        
//                        tuConn.setConnectTimeout(100);
//                        //tuConn.setUseCaches(false);
//                        //tuConn.setDoOutput(false);
//                        tuConn.setDoInput(true);
//                        tuConn.setAllowUserInteraction(false);
//                        tuConn.setRequestMethod("GET");
//
//                        // Fake environment from Chrome
//                        tuConn.setRequestProperty("Connection", "Keep-alive");
//                        tuConn.setRequestProperty("Cache-Control", "max-age=0");
//                        tuConn.setRequestProperty("Origin", "https://osu.ppy.sh");
//                        tuConn.setRequestProperty("Upgrade-Insecure-Requests", "0");
//                        tuConn.setRequestProperty("User-Agent",
//                                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
//                        tuConn.setRequestProperty("Accept",
//                                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//                        tuConn.setRequestProperty("DNT", "1");
//                        tuConn.setRequestProperty("Accept-Encoding", "gzip"); //Accept gzip encoding
//                        tuConn.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.8,en;q=0.6");
//                        
//                        String tuEncoding = tuConn.getHeaderField("Content-Encoding");
//                        InputStream tuIn;
//                        if (tuEncoding != null && tuEncoding.equals("gzip")){
//                            tuIn = new GZIPInputStream(tuConn.getInputStream());
//                        } else {
//                            tuIn = tuConn.getInputStream();
//                        }
//                        
//                        BufferedImage image = ImageIO.read(tuIn);
//                        
//                        //ByteArrayOutputStream b = new ByteArrayOutputStream();
//                        //ImageIO.write(image, "jpg", b);
//                        thumbData = Base64.encodeBase64String(IOUtils.toByteArray(tuIn));
//                        tuIn.close();
//                    } catch (IOException ignore){
//                        ignore.printStackTrace();
//                    }
                    
                    maps.add(new ResultBeatmap(
                            id, escape(artist), escape(title),
                            escape(creator), tags, -1,
                            -1, "https://osu.ppy.sh" + titleEl.attr("href"),
                            thumbUrl, thumbData
                            ));
                    
                    handler.setBeatmapIndexed(maps.size());
                }
                
                handler.setCompletedPages(i);
                handler.onLoopEnd();
            }
            
            ResultBeatmap[] out = new ResultBeatmap[maps.size()];
            
            for (int i = 0; i < out.length; i++){
                out[i] = maps.get(i);
            }
            
            handler.onComplete();
            
            return out;
        } catch (Exception e) {
            handler.onError();
            throw new WithDumpException(searchLink, "(Try&catch try) getting search result links",
                    "Throw debuggable exception on catch", "(End of function)",
                    "Error occurred when getting search result links", false, e);
        }
    }
    
    private static String escape(String str){
        try {
            return URLEncoder.encode(str, "UTF-8").replaceAll("%(..)%(..)", "%u$1$2");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public OsuBeatmap getBeatmapInfo(String beatmapLink) throws WithDumpException {
        try {
            URL url = new URL(beatmapLink);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (cmgr.getCookieStore().getCookies().size() > 0) {
                // While joining the Cookies, use ',' or ';' as needed. Most of
                // the servers are using ';'
                conn.setRequestProperty("Cookie", join(";", cmgr.getCookieStore().getCookies()));
            }

            conn.setUseCaches(false);
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");

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

            String data = "";
            String line;
            
            String encoding = conn.getHeaderField("Content-Encoding");
            InputStream in;
            if (encoding != null && encoding.equals("gzip")){
                in = new GZIPInputStream(conn.getInputStream());
            } else {
                in = conn.getInputStream();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            while ((line = reader.readLine()) != null) {
                data += line;
            }

            Map<String, List<String>> headerFields = conn.getHeaderFields();
            
            Iterator<String> it = headerFields.keySet().iterator();
            String key;
            while (it.hasNext()) {
                key = it.next();
                if (key != null && key.toLowerCase().equals("location")) {
                    new Thread() {
                        public void run() {
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showInputDialog("Sorry, this beatmap redirects to new osu! layout page, which is not supported currently.\nYou are recommended to enable old-site redirection in Preferences to fix this problem temporarily.\nPlease manually download it in a browser:", beatmapLink);
                        }
                    }.start();
                    return null;
                }
            }
            
            List<String> cookiesHeader = headerFields.get("Set-Cookie");

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    cmgr.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }

            Document doc = Jsoup.parse(data);
            return OsuBeatmap.createInstance(beatmapLink, doc);
            /*
             * Elements elements =
             * doc.getElementsByClass("beatmap_download_link");
             * 
             * if (elements.size() == 0){ throw new WithDumpException(
             * "No download link available. Invalid beatmap url?"); }
             * 
             * Element alnk = elements.get(0);
             * 
             * String href = alnk.attr("href");
             * 
             * return href;
             */
        } catch (Exception e) {
            throw new WithDumpException(beatmapLink, "(Try&catch try) getting beatmap info",
                    "Throw debuggable exception on catch", "(End of function)",
                    "Error occurred when getting beatmap info", false, e);
        }
    }
    
    public boolean isLoggedIn() {
        try {
            return loggedIn && testLoggedIn();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean testLoggedIn() throws IOException{
        URL url = new URL(LOGIN_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setInstanceFollowRedirects(false);

        if (cmgr.getCookieStore().getCookies().size() > 0) {
            // While joining the Cookies, use ',' or ';' as needed. Most of
            // the servers are using ';'
            conn.setRequestProperty("Cookie", join(";", cmgr.getCookieStore().getCookies()));
        }

        // Fake environment from Chrome
        conn.setRequestProperty("Connection", "Keep-alive");
        conn.setRequestProperty("Cache-Control", "max-age=0");
        conn.setRequestProperty("Origin", "https://osu.ppy.sh");
        conn.setRequestProperty("Upgrade-Insecure-Requests", "0");
        conn.setRequestProperty("User-Agent",
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
        conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        conn.setRequestProperty("DNT", "1");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        conn.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.8,en;q=0.6");
        
        Map<String, List<String>> headerFields = conn.getHeaderFields();

        List<String> locationHeader = headerFields.get("Location");
        
        System.out.println(locationHeader.get(0));

        if (locationHeader == null || locationHeader.size() != 1
                || !locationHeader.get(0).startsWith(INDEX_LOCATION_NO_INDEXPHP_URL)) {
            return false;
        } else {
            return true;
        }
    }

    public int login(String username, String password) throws WithDumpException {
        if (loggedIn) {
        	System.out.println("Already logged");
            return ALREADY_LOGGED_IN;
        }
        try {
            String urlPara = "login=Login&" +
                    "username=" + username + "&" + // Username
                    "password=" + password + "&" + // Password
                    "autologin=" + true + "&" + // Log me on automatically each
                                                // visit
                    "viewonline=" + true + "&" + // Hide my online this session
                    "redirect=" + "index.php" + "&" + // Redirect (To check
                                                      // whether the login is
                                                      // success)
                    "sid="; // Session ID, not distributed once

            byte[] postData = urlPara.getBytes(StandardCharsets.UTF_8);
            int postLen = postData.length;

            URL url = new URL(LOGIN_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setInstanceFollowRedirects(false);

            if (cmgr.getCookieStore().getCookies().size() > 0) {
                // While joining the Cookies, use ',' or ';' as needed. Most of
                // the servers are using ';'
                conn.setRequestProperty("Cookie", join(";", cmgr.getCookieStore().getCookies()));
            }

            conn.setDoOutput(true);
            conn.setUseCaches(false);

            // Fake environment from Chrome
            conn.setRequestProperty("Connection", "Keep-alive");
            conn.setRequestProperty("Cache-Control", "max-age=0");
            conn.setRequestProperty("Origin", "https://osu.ppy.sh");
            conn.setRequestProperty("Upgrade-Insecure-Requests", "0");
            conn.setRequestProperty("User-Agent",
                    "User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            conn.setRequestProperty("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            conn.setRequestProperty("DNT", "1");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            conn.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.8,en;q=0.6");

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postLen));

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(dos, "UTF-8"));
            wr.write(urlPara);
            wr.close();

            /*
             * String data = ""; String line; BufferedReader reader = new
             * BufferedReader(new InputStreamReader(conn.getInputStream()));
             * 
             * while((line = reader.readLine()) != null){ data += line + "\n"; }
             */

            Map<String, List<String>> headerFields = conn.getHeaderFields();

            List<String> locationHeader = headerFields.get("Location");

            if (locationHeader == null || locationHeader.size() != 1
                    || !locationHeader.get(0).startsWith(INDEX_LOCATION_URL)) {
                throw new WithDumpException("", "Get \"Location\" from headerFields and assign to locationHeader",
                        "Validate locationHeader is not null or locationHeader.size() is 1 or locationHeader.get(0) is equal to INDEX_LOCATION_URL",
                        "Get \"Set-Cookie\" from headerFields and assign to cookiesHeader",
                        "Login failed. Redirected to a non-index page.", true);
            }

            List<String> cookiesHeader = headerFields.get("Set-Cookie");

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    cmgr.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }

            loggedIn = true;

            return SUCCESS;
        } catch (Exception e) {
            throw new WithDumpException("", "(Try&catch try) Logging in", "Throw debuggable exception on catch",
                    "(End of function)", "Error occurred when logging in", true, e);
        }
    }

    public int logout(String sid) throws WithDumpException {
        try {
            String urlPara = "sid=" + sid; // Session ID

            URL url = new URL(LOGOUT_URL + "?" + urlPara);
            URLConnection conn = url.openConnection();

            if (cmgr.getCookieStore().getCookies().size() > 0) {
                // While joining the Cookies, use ',' or ';' as needed. Most of
                // the servers are using ';'
                conn.setRequestProperty("Cookie", join(";", cmgr.getCookieStore().getCookies()));
            }

            conn.setUseCaches(false);

            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", "0");

            Map<String, List<String>> headerFields = conn.getHeaderFields();

            List<String> cookiesHeader = headerFields.get("Set-Cookie");

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    cmgr.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }

            String data = "";
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = reader.readLine()) != null) {
                data += line;
            }

            System.out.println(data);
            return SUCCESS;
        } catch (Exception e) {
            throw new WithDumpException("", "(Try&catch try) Logging out", "Throw debuggable exception on catch",
                    "(End of function)", "Error occurred when logging out", true, e);
        }
    }

    public static boolean isVaildBeatMapUrl(String urlstr) {
        return (urlstr
                .length() > URL_PREFIX.length()
                        + 2
                && (urlstr.substring(0, URL_PREFIX.length() + 2).equals(URL_PREFIX + BEATMAP_DIR)
                        || urlstr.substring(0, URL_PREFIX.length() + 2).equals(URL_PREFIX + SONG_DIR)))
                || (urlstr.length() > URL_PREFIX_SSL.length() + 2
                        && (urlstr.substring(0, URL_PREFIX_SSL.length() + 2).equals(URL_PREFIX_SSL + BEATMAP_DIR)
                                || urlstr.substring(0, URL_PREFIX_SSL.length() + 2).equals(URL_PREFIX_SSL + SONG_DIR)));
    }

    public CookieManager getCookies() {
        return cmgr;
    }

    private static String join(String separator, List<HttpCookie> objs) {
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

    public static int updateSourceStrToInt(String branchStr) {
        if (branchStr.equals("snapshot")) {
            return 2;
        } else if (branchStr.equals("beta")) {
            return 1;
        } else if (branchStr.equals("stable")) {
            return 0;
        }
        return -1;
    }

    public OnlineIndexManager getOimgr() {
        return oimgr;
    }

}
