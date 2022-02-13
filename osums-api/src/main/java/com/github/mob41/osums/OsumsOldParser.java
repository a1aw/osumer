package com.github.mob41.osums;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.mob41.osumer.debug.WithDumpException;
import com.github.mob41.osums.beatmap.OsuBeatmap;
import com.github.mob41.osums.beatmap.OsuSong;
import com.github.mob41.osums.search.SongResult;
import com.github.mob41.osums.search.RankFilter;
import com.github.mob41.osums.search.SearchFilter;
import com.github.mob41.osums.search.SearchResult;

/***
 * The old osu!-web parser, that uses "old.ppy.sh" as end-point
 * @author Anthony
 *
 */
public class OsumsOldParser extends Osums {

    private static final String LOGOUT_URL = "https://old.ppy.sh/forum/ucp.php?mode=logout";

    private static final String LOGIN_URL = "https://old.ppy.sh/forum/ucp.php?mode=login";
    
    private static final String SEARCH_URL = "https://old.ppy.sh/p/beatmaplist?q=";

    private static final String INDEX_LOCATION_URL = "https://old.ppy.sh/forum/index.php";

    private static final String URL_PREFIX = "http://osu.ppy.sh/";

    private static final String URL_PREFIX_SSL = "https://osu.ppy.sh/";

    private static final String BEATMAP_DIR = "b/";

    private static final String SONG_DIR = "s/";
	
    private static final String BEATMAP_NEW = "beatmaps/";

    private static final String SONG_NEW = "beatmapsets/#osu/";
	
    private static final String BEATMAPSETS_NEW = "beatmapsets/";
	
	private static final int RANK_RANKED_INT = 1;
	
	private static final int RANK_PENDING_INT = 2;
	
	private static final int RANK_ALL_INT = 4;
	
	private static final int RANK_QUALIFIED_INT = 11;
	
	private static final int RANK_LOVED_INT = 12;
    
    private boolean loggedIn = false;

	@Override
	public SearchResult searchOnlineMaps(String keywords, SearchFilter[] filters, int page) throws WithDumpException {
		if (keywords == null) {
			keywords = "";
		}
		
		String url = SEARCH_URL + keywords;
		
		String rank = null;
		if (filters != null) {
			for (SearchFilter filter : filters) {
				if (filter instanceof RankFilter) {
					rank = ((RankFilter) filter).getRank();
				}
				url = filter.handleUrl(url);
			}
		}
		
		if (rank == null) {
			rank = RANK_ANY;
		}
		
		if (rank.equals(RANK_RANKED)) {
			url += "&r=" + RANK_RANKED_INT;
		} else if (rank.equals(RANK_PENDING)) {
			url += "&r=" + RANK_PENDING_INT;
		} else if (rank.equals(RANK_QUALIFIED)) {
			url += "&r=" + RANK_QUALIFIED_INT;
		} else if (rank.equals(RANK_LOVED)) {
			url += "&r=" + RANK_LOVED_INT;
		} else if (rank.equals(RANK_ANY)) {
			url += "&r=" + RANK_ALL_INT;
		} else {
			System.out.println("Unknown beatmap rank: " + rank);
		}
		
        try {
    		String data = getHttpCookiedContent(url + "&page=" + page);
    		
    		Document doc = Jsoup.parse(data);
    		
    		Element pagination = doc.getElementsByClass("pagination").first();
            String paginationText = pagination.html();
            
            int totalPages = 1;
            
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
            
            if (totalResultMaps != -1 && currPageMaps != -1 &&
                    currPageMaps != totalResultMaps){
                
                currPageMaps -= 40 * (page - 1);
                
                //System.out.println("Using method 1 to identify Total Pages");
                //System.out.println("TRM/CPM: " + totalResultMaps + " / " + currPageMaps);
                float calc = ((float) totalResultMaps / currPageMaps);
                totalPages = (int) calc;
                
                if (calc != totalPages){
                    //System.out.println("Calc != totalPages: " + calc + " != " + totalPages);
                    totalPages++;
                }

                //System.out.println("Now total pages: " + totalPages);
            } else {
                //System.out.println("Using method 2 to identify Total Pages");
                Elements pageLinkEls = pagination.children();
                
                int size = pageLinkEls.size();
                
                if (size < 2){
                    totalPages = 1;
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
                            //System.out.println("Last page num is bigger than total pages: " + lastPageNum + " > " + totalPages);
                            totalPages = lastPageNum;
                        } else {
                            //System.out.println("Last page num is sammler than total pages: " + lastPageNum + " < " + totalPages);
                        }
                    }
                }
            }
    		
    		SongResult[] results = parseSearch(rank, doc);
    		return new SearchResult(results, page, totalPages);
        } catch (Exception e) {
            throw new WithDumpException(url, "(Try&catch try) getting search result links",
                    "Throw debuggable exception on catch", "(End of function)",
                    "Error occurred when getting search result links", false, e);
        }
	}

	@Override
	public int login(String username, String password) throws WithDumpException {
        if (loggedIn) {
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
            HttpURLConnection conn = prepareUrlConnectionPost(url);

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postLen));
            conn.setRequestProperty("Origin", "https://old.ppy.sh");
            conn.setRequestProperty("Referer", "https://old.ppy.sh");
            
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(dos, "UTF-8"));
            wr.write(urlPara);
            wr.close();

            Map<String, List<String>> headerFields = conn.getHeaderFields();
            List<String> locationHeader = headerFields.get("Location");
            
            if (locationHeader == null || locationHeader.size() != 1
                    || !locationHeader.get(0).startsWith(INDEX_LOCATION_URL)) {
                throw new WithDumpException("", "Get \"Location\" from headerFields and assign to locationHeader",
                        "Validate locationHeader is not null or locationHeader.size() is 1 or locationHeader.get(0) is equal to INDEX_LOCATION_URL",
                        "Get \"Set-Cookie\" from headerFields and assign to cookiesHeader",
                        "Login failed. Redirected to a non-index page.", true);
            }

            retrieveCookies(headerFields);

            loggedIn = true;

            return SUCCESS;
        } catch (Exception e) {
            throw new WithDumpException("", "(Try&catch try) Logging in", "Throw debuggable exception on catch",
                    "(End of function)", "Error occurred when logging in", true, e);
        }
	}

	@Override
	public boolean logout() throws WithDumpException{
		try {
			getHttpCookiedContent(LOGOUT_URL + "?sid=");
            return true;
        } catch (Exception e) {
            throw new WithDumpException("", "(Try&catch try) Logging out", "Throw debuggable exception on catch",
                    "(End of function)", "Error occurred when logging out", true, e);
        }
	}

	@Override
	public boolean isLoggedIn() {
		return loggedIn;
	}

	@Override
	public boolean checkLoggedIn() throws IOException{
        URL url = new URL(LOGIN_URL);
        HttpURLConnection conn = prepareUrlConnectionGet(url);
        
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> locationHeader = headerFields.get("Location");

        if (locationHeader == null || locationHeader.size() != 1
                || !locationHeader.get(0).startsWith(INDEX_LOCATION_URL)) {
            return false;
        } else {
            return true;
        }
	}

	@Override
	public OsuSong getSongInfo(String link) throws WithDumpException {
		try {
			link = link.replaceFirst("osu.ppy.sh", "old.ppy.sh");
			link = link.replaceFirst("beatmaps\\w*/\\d+\\D+", "b/");
			link = link.replaceFirst("beatmaps", "b");
			link = link.replaceFirst("beatmapsets", "s");
			
            String data = getHttpCookiedContent(link);
            
            Document doc = Jsoup.parse(data);
            return parseSong(link, doc);
        } catch (Exception e) {
            throw new WithDumpException(link, "(Try&catch try) getting beatmap info",
                    "Throw debuggable exception on catch", "(End of function)",
                    "Error occurred when getting beatmap info", false, e);
        }
	}

	@Override
	public OsuBeatmap getBeatmapInfo(String link) throws WithDumpException {
//		if (!link.contains("/b/") || !link.contains("/beatmaps/")) {
//			throw new WithDumpException(link, "(Start of function)", "Checking if link is beatmap url", "Start parsing", "The link provided is not a beatmap url", false);
//		}
		return (OsuBeatmap) getSongInfo(link);
	}

	@Override
	public SongResult[] parseSearch(String category, Document doc) throws WithDumpException {
        List<SongResult> maps = new ArrayList<SongResult>();
        
		Element beatmapsDiv = doc.select("div.beatmapListing").first();
        Iterator<Element> it = beatmapsDiv.children().iterator();
        
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
            
            maps.add(new SongResult(
                    id, category, artist, title,
                    creator, tags, -1,
                    -1));
        }
        
        SongResult[] out = new SongResult[maps.size()];
        
        for (int i = 0; i < out.length; i++){
            out[i] = maps.get(i);
        }
        
        return out;
	}

	@Override
	public OsuSong parseSong(String originalUrl, Document doc) throws WithDumpException {
		boolean pageBeatmap = originalUrl.contains("/b/");
	    
		Elements dwnLinks = doc.getElementsByClass("beatmap_download_link");
		
		if (dwnLinks.size() == 0){
			throw new WithDumpException(
					"",
					"Get elements by class \"beatmap_download_link\"",
					"Validate download link element. Throw exception if size() equal to 0",
					"Get the first element of the download links elements",
					"No element with class \"beatmap_download_link\", which should be at least one in a beatmap info page. Invalid beatmap URL?",
					true);
		}
		
		Element alnk = dwnLinks.get(0);
		
		String dwnLnk = alnk.attr("href");
		
		Element contentWithBgEl = doc.select("div.content-with-bg").first();
		
		if (contentWithBgEl == null){
			throw new WithDumpException(
					"",
					"Select \"div.content-with-bg\" and get the first element, assign to contentWithBgEl",
					"Validate contentWithBgEl is null or not",
					"Select \"div#tablist ul\" and get the first element, assign to tabList",
					"No div element with class \"content-with-bg\"",
					true);
		}
		
		//Element header1 = contentWithBgEl.select("h1").first();
		
		Element tabList = contentWithBgEl.select("div#tablist ul").first();
		
		if (tabList == null){
			throw new WithDumpException(
					"",
					"Select \"div#tablist ul\" and get the first element, assign to tabList",
					"Validate tabList is null or not",
					"Get tabList children and assign to tabListLi",
					"No div element with class \"tablist\" with ul",
					true);
		}
		
		Elements tabListLi = tabList.children();
		
		/*
		String[] tabStr = new String[tabListLi.size()];
		for (int i = 0; i < tabListLi.size(); i++){
			//<a> tag should be inside the <li> according to online code
			Element el = tabListLi.get(i).select(".beatmapTab span").first();
			
			if (el == null){
				throw new WithDumpException(
						tabListLi.get(i).html(),
						"(Loop of element tabList children) Select \".beatmapTab span\" from children and get the first element, assign to el",
						"Validate el is null or not",
						"Get the inner HTML of <span>",
						"No <a>/<span> tag in the difficulties tab index " + i + ". Page broke?",
						false);
			}
			
			tabStr[i] = el.html();
		}
		*/
		
		Element songInfo = contentWithBgEl.select("div table#songinfo tbody").first();
		
		if (songInfo == null){
			throw new WithDumpException(
					"",
					"Select \"div table#songinfo tbody# in contentWithBgEl and get the first element, assign to songInfo",
					"Validate songInfo is null or not",
					"Get songInfo children",
					"No songinfo element in contentWithBgEl. Page broke?",
					true);
		}
		
		Elements songInfoTrs = songInfo.children();
		
		if (songInfoTrs.size() < 7){
			throw new WithDumpException(
					songInfoTrs.html(),
					"Get songInfo children",
					"Validate songInfo is enough <tr> elements (more than 7)",
					"(Lots of code) Get song info data from table",
					"No song info table <tr> / not enough <tr>. Only " + songInfoTrs.size() + "/7 Page broke?",
					false);
		}
		
		String[] tags = null;
		String genre = null;
		String source = null;
		String creator = null;
		String artist = null;
		String title = null;
		float circleSize = -1;
		float approachRate = -1;
		float hpDrain = -1;
		float accuracy = -1;
		float starDifficulty = -1;
		float bpm = -1;
		int good_rating = -1;
		int bad_rating = -1;
		float success_rate = -1;
		for (int i = 0; i < songInfoTrs.size(); i++){
			Elements tr = songInfoTrs.get(i).children();
			Element el;
			Element el_a;
			switch (i){ //even number index <td> elements are labels
			case 0: //[1]Artist[3]Circle size (starfield)[5]Approach rate(starfield)
				for (int j = 0; j < tr.size(); j++){ //tr.size() should be 6
					el = tr.get(j);
					switch (j){
					case 0:
						break;
					case 1:
						el_a = el.select("a[href]").first();
						if (el_a == null){
							throw new WithDumpException(
									el.html(),
									"Select \"a[href]\" from el and assign to el_a",
									"Validate el_a is null or not",
									"Get inner HTML from <a> and assign to artist",
									"No artist related data. Page broke?",
									false);
						}
						artist = el_a.html();
						break;
					case 2:
						break;
					case 3: //Circle size (Starfield)
					    if (pageBeatmap){
	                        circleSize = parseStarfield("Circle Size", el);
					    }
						break;
					case 4:
						break;
					case 5: //Approach rate (Starfield)
                        if (pageBeatmap){
                            approachRate = parseStarfield("Approach Rate", el);
                        }
						break;
					}
				}
				break;
			case 1: //[1]Title[3]HP Drain(starfield)[5]Star difficulty(starfield)
				for (int j = 0; j < tr.size(); j++){ //tr.size() should be 6
					el = tr.get(j);
					switch (j){
					case 0:
						break;
					case 1:
						el_a = el.select("a[href]").first();
						if (el_a == null){
							throw new WithDumpException(
									el.html(),
									"Select \"a[href]\" from el and assign to el_a",
									"Validate el_a is null or not",
									"Get inner HTML from <a> and assign to title",
									"No title related data. Page broke?",
									false);
						}
						title = el_a.html();
						break;
					case 2:
						break;
					case 3: //HP Drain (Starfield)
                        if (pageBeatmap){
                            hpDrain = parseStarfield("HP Drain", el);
                        }
						break;
					case 4:
						break;
					case 5: //Star Diff (Starfield)
                        if (pageBeatmap){
                            starDifficulty = parseStarfield("Star Difficulty", el);
                        }
						break;
					}
				}
				break;
			case 2: //[1]Creator[3]Accuracy(starfield)[5]Length (format: x:xx (x:xx drain))
				for (int j = 0; j < tr.size(); j++){ //tr.size() should be 6
					el = tr.get(j);
					switch (j){
					case 0:
						break;
					case 1:
						el_a = el.select("a[href]").first();
						if (el_a == null){
							throw new WithDumpException(
									el.html(),
									"Select \"a[href]\" from el and assign to el_a",
									"Validate el_a is null or not",
									"Get inner HTML from <a> and assign to creator",
									"No creator related data. Page broke?",
									false);
						}
						creator = el_a.html();
						break;
					case 2:
						break;
					case 3: //Accuracy (Starfield)
                        if (pageBeatmap){
                            accuracy = parseStarfield("Accuracy", el);
                        }
						break;
					case 4:
						break;
					case 5: //Length (in format: x:xx (x:xx drain)) (figuring to get)
						break;
					}
				}
				break;
			case 3: //[1]Source[3]Genre[5]BPM
				for (int j = 0; j < tr.size(); j++){ //tr.size() should be 6
					el = tr.get(j);
					switch (j){
					case 0:
						break;
					case 1:
						el_a = el.select("a[href]").first();
						if (el_a == null){
							throw new WithDumpException(
									el.html(),
									"Select \"a[href]\" from el and assign to el_a",
									"Validate el_a is null or not",
									"Get inner HTML from <a> and assign to source",
									"No source related data. Page broke?",
									false);
						}
						source = el_a.html();
						break;
					case 2:
						break;
					case 3: //Format: xx (xx), however we are not getting (xx)
						el_a = el.select("a[href]").first();
						if (el_a == null){
							throw new WithDumpException(
									el.html(),
									"Select \"a[href]\" from el and assign to el_a",
									"Validate el_a is null or not",
									"Get inner HTML from <a> and assign to genre",
									"No genre related data. Page broke?",
									false);
						}
						genre = el_a.html();
						break;
					case 4:
						break;
					case 5: //Floating BPM
						if (el == null){
							throw new WithDumpException(
									null,
									"(Loop, switch)",
									"Validate el is null or not",
									"Parse float from el inner HTML",
									"No bpm related data. Page broke?",
									false);
						}
						try {
							bpm = Float.parseFloat(el.html().replaceAll(",", ""));
						} catch (NumberFormatException e){
							throw new WithDumpException(
									el.html(),
									"Validate el is null or not",
									"Parse float from el inner HTML",
									"(Break switch, Loop)",
									"Cannot parse float. BPM is not a floating number",
									false, e);
						}
						break;
					}
				}
				break;
			case 4:
				for (int j = 0; j < tr.size(); j++){ //tr.size() should be 6
					el = tr.get(j);
					switch (j){
					case 0:
						break;
					case 1:
						Elements tagsEls = el.select("a[href]");
						if (tagsEls.size() == 0){
							throw new WithDumpException(
									el.html(),
									"Select \"a[href]\" from el and assign to el_a",
									"Validate el_a is null or not",
									"Get inner HTML from <a> and assign to tags",
									"No tags related data. Page broke?",
									false);
						}

						tags = new String[tagsEls.size()];
						for (int x = 0; x < tags.length; x++){
							tags[x] = tagsEls.get(x).html();
						}
						break;
					case 2:
						break;
					case 3: //Well this is tricky, a bad rating in the first <td>, and the good one in the second <td>
						Elements ratingEls = el.select("table tbody tr td");
						if (ratingEls.size() < 2){
							throw new WithDumpException(
									el.html(),
									"Select \"table tbody tr td\" from el and assign to ratingEls",
									"Validate ratingEls elements is enough (more than 2)",
									"Get the first element in ratingEls and assign to el_a",
									"Not enough " + ratingEls.size() + "/2 / no rating related data. Page broke?",
									false);
						}
						el_a = ratingEls.get(0);
						try {
							bad_rating = Integer.parseInt(el_a.html().replaceAll(",", ""));
						} catch (NumberFormatException e){
							throw new WithDumpException(
									el_a.html(),
									"Get the first element in ratingEls and assign to el_a",
									"Parse integer from el_a inner HTML",
									"Get the second element in ratingEls and assign to el_a",
									"Cannot parse integer. Bad rating is not an integer",
									false, e);
						}
						el_a = ratingEls.get(1);
						try {
							good_rating = Integer.parseInt(el_a.html().replaceAll(",", ""));
						} catch (NumberFormatException e){
							throw new WithDumpException(
									el_a.html(),
									"Get the second element in ratingEls and assign to el_a",
									"Parse integer from el_a inner HTML",
									"(Break switch, Loop)",
									"Cannot parse integer. Good rating is not an integer",
									false, e);
						}
						break;
					case 4:
						break;
					case 5: //Floating success rate
						el_a = el.select("td").first();
						if (el_a != null && el_a.html().contains("Not yet played!")){
							success_rate = -1;
							break;
						}
						
						el_a = el.select("td b").first();
						if (el_a == null){
							throw new WithDumpException(
									el.html(),
									"Select \"td b\" from el and assign to el_a",
									"Validate el_a is null or not",
									"(Try&catch scope) Get inner HTML from <a> and assign to str",
									"No success rate related data. Page broke?",
									false);
						}
						try {
							String str = el_a.html();
							int index = str.indexOf("%");
							success_rate = Float.parseFloat(str.substring(0, index));
						} catch (NumberFormatException e){
							throw new WithDumpException(
									el_a.html(),
									"Validate el_a is null or not",
									"Parse float from el_a inner HTML",
									"(Break switch, Loop)",
									"Cannot parse float. Success rate is not a floating number",
									false, e);
						}
						break;
					}
				}
				break;
			case 5: //figuring out
				break;
			case 6: //figuring out
				break;
			}
		}
		
		Element thumbEl = contentWithBgEl.select("div.paddingboth div.posttext a[href] img[src].bmt").first();
		
		if (thumbEl == null){
			throw new WithDumpException(
					"",
					"Select \"div.paddingboth div.posttext a[href] img[src].bmt\" from contentWithBgEl and assign to thumbEl",
					"Validate thumbEl is null or not",
					"Get attribute \"src\" from thumbEl",
					"No thumb image related data. Page broke?",
					true);
		}
		
		String thumbUrl = thumbEl.attr("src");
		
		if (pageBeatmap) {
			return new OsuBeatmap(originalUrl, artist + " - " + title, title, artist, creator, source,
					genre, dwnLnk, thumbUrl, bad_rating, good_rating, bpm, circleSize, approachRate, hpDrain, accuracy, starDifficulty,
					  success_rate, pageBeatmap);
		} else {
			return new OsuSong(originalUrl, artist + " - " + title, title, artist, creator, source,
					genre, dwnLnk, thumbUrl, bad_rating, good_rating, bpm, pageBeatmap);
		}
	}
	
	private static float parseStarfield(String debugElementName, Element el) throws WithDumpException{
	    Element el_a = el.select("div.starfield").first();
        if (el_a == null){
            throw new WithDumpException(
                    el.html(),
                    "Select \"div.starfield\" from el and assign to el_a",
                    "Validate el_a is null or not",
                    "Get style total width",
                    "No " + debugElementName + " (starfield) related data. Page broke?",
                    false);
        }
        String styleStr = el_a.attr("style");
        int indexPx = styleStr.indexOf("px"); //TODO Do PX validation
        float totalWidth = -1;
        try {
            totalWidth = Float.parseFloat(styleStr.substring(6, indexPx));
        } catch (NumberFormatException e){
            throw new WithDumpException(
                    el.html(),
                    "Validate el_a is null or no",
                    "Get style total width",
                    "Select \"div.active\" from el_a and assign to el_a",
                    "Invalid float value: " + styleStr,
                    false, e);
        }
        
        el_a = el_a.select("div.active").first();
        if (el_a == null){
            throw new WithDumpException(
                    el.html(),
                    "Select \"div.active\" from el_a and assign to el_a",
                    "Validate el_a is null or not",
                    "Get style width of starfield active",
                    "No " + debugElementName + " active (starfield active) related data. Page broke?",
                    false);
        }
        
        styleStr = el_a.attr("style");
        indexPx = styleStr.indexOf("px");
        float activeWidth = -1;
        try {
            activeWidth = Float.parseFloat(styleStr.substring(6, indexPx));
        } catch (NumberFormatException e){
            throw new WithDumpException(
                    el.html(),
                    "Validate el_a is null or no",
                    "Get style active width",
                    "Calculate " + debugElementName,
                    "Invalid float value: " + styleStr,
                    false, e);
        }
        System.out.println(debugElementName + ": " + activeWidth / totalWidth * 10);
        return activeWidth / totalWidth * 10;
	}

	@Override
	public boolean isVaildBeatmapUrl(String url) {
		return checkVaildBeatmapUrl(url);
	}

	public static boolean checkVaildBeatmapUrl(String url) {
		return (url
                .length() > URL_PREFIX.length()
                        + 2
                && (url.substring(0, URL_PREFIX.length() + 2).equals(URL_PREFIX + BEATMAP_DIR)
                        || url.substring(0, URL_PREFIX.length() + 2).equals(URL_PREFIX + SONG_DIR)))
                || (url.length() > URL_PREFIX_SSL.length() + 2
                        && (url.substring(0, URL_PREFIX_SSL.length() + 2).equals(URL_PREFIX_SSL + BEATMAP_DIR)
                                || url.substring(0, URL_PREFIX_SSL.length() + 2).equals(URL_PREFIX_SSL + SONG_DIR)
								|| url.substring(0, URL_PREFIX_SSL.length() + 9).equals(URL_PREFIX_SSL + BEATMAP_NEW)
								|| url.substring(0, URL_PREFIX_SSL.length() + 17).equals(URL_PREFIX_SSL + SONG_NEW)
								|| url.substring(0, URL_PREFIX_SSL.length() + 12).equals(URL_PREFIX_SSL + BEATMAPSETS_NEW)));
    
	}

}
