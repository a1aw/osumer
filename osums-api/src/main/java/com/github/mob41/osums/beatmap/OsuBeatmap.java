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
package com.github.mob41.osums.beatmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.codec.binary.Base64;
//import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.mob41.osumer.debug.WithDumpException;

public class OsuBeatmap extends OsuSong{

	private final String originalUrl;
	
	private final String name;
	
	//private final String[] difficulties;
	
	private final String title;
	
	private final String artist;
	
	private final String creator;
	
	private final String source;
	
	private final String genre;
	
	private final String dwnUrl;
	
	private final String thumbUrl;
	
	private final float circleSize;
	
    private final float approachRate;
    
    private final float hpDrain;
    
    private final float accuracy;
	
	private final float starDifficulty;
	
	private final int bad_rating;
	
	private final int good_rating;
	
	private final float bpm;
	
	private final float success_rate;
	
	private final boolean pageBeatmap;

	private OsuBeatmap(String originalUrl, String name, String title,  //... String name, String[] difficulties ...
			String artist, String creator, String source, String genre, String dwnUrl,
			String thumbUrl, float circleSize, float approachRate, float hpDrain,
			float acurracy, float starDifficulty, int badRating, int goodRating,
			float bpm, float successRate, boolean pageBeatmap) {
		this.originalUrl = originalUrl;
		this.name = name;
		//this.difficulties = difficulties;
		this.title = title;
		this.artist = artist;
		this.creator = creator;
		this.source = source;
		this.genre = genre;
		this.dwnUrl = dwnUrl;
		this.thumbUrl = thumbUrl;
		this.circleSize = circleSize;
		this.approachRate = approachRate;
		this.hpDrain = hpDrain;
		this.accuracy = acurracy;
		this.starDifficulty = starDifficulty;
		this.bad_rating = badRating;
		this.good_rating = goodRating;
		this.bpm = bpm;
		this.success_rate = successRate;
		this.pageBeatmap = pageBeatmap;
	}
	
	public String getName(){
		return name;
	}
	
	//TODO Unnecessary for a beatmap URL instead of song URL
	/*
	public String[] getDifficulties(){
		return difficulties;
	}
	*/
	
	public String getTitle(){
		return title;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getCreator() {
		return creator;
	}

	public String getSource() {
		return source;
	}

	public String getGenre() {
		return genre;
	}

	public String getDwnUrl() {
		return dwnUrl;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public float getStarDifficulty() {
		return starDifficulty;
	}

	public int getBadRating() {
		return bad_rating;
	}

	public int getGoodRating() {
		return good_rating;
	}
	
	public float getRating(){
		return (float) good_rating / (bad_rating + good_rating) * 100;
	}

	public float getBpm() {
		return bpm;
	}

	public float getSuccessRate() {
		return success_rate;
	}

    public String getOriginalUrl() {
        return originalUrl;
    }

    public float getCircleSize() {
        return circleSize;
    }

    public float getApproachRate() {
        return approachRate;
    }

    public float getHpDrain() {
        return hpDrain;
    }

    public float getAccuracy() {
        return accuracy;
    }

	public static OsuBeatmap createInstance(String originalUrl, Document doc) throws WithDumpException{
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
		
		return new OsuBeatmap(originalUrl, artist + " - " + title, title, artist, creator, source,
				genre, dwnLnk, thumbUrl, circleSize, approachRate, hpDrain, accuracy, starDifficulty, bad_rating,
				good_rating, bpm, success_rate, pageBeatmap);
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
	
	public static boolean equalsArr(String[] arg0, String[] arg1){
		if (arg0 == null && arg1 == null){
			return true;
		} else if (arg0 == null || arg1 == null) {
			return false;
		}
		
		if (arg0.length != arg1.length){
			return false;
		}
		
		for (int i = 0; i < arg0.length; i++){
			if (!arg0[i].equals(arg1[i])){
				return false;
			}
		}
		return true;
	}
	
	public boolean equals(OsuBeatmap map){
		return getArtist().equals(map.getArtist()) &&
				getBadRating() == map.getBadRating() &&
				getBpm() == map.getBpm() &&
				getCreator().equals(map.getCreator()) &&
				//equalsArr(getDifficulties(), map.getDifficulties()) &&
				getDwnUrl().equals(map.getDwnUrl()) &&
				getGenre().equals(map.getGenre()) &&
				getGoodRating() == map.getGoodRating() &&
				getName().equals(map.getName()) &&
				getRating() == map.getRating() &&
				getSource().equals(map.getSource()) &&
				getCircleSize() == map.getCircleSize() &&
				getApproachRate() == map.getApproachRate() &&
				getHpDrain() == map.getHpDrain() &&
				getAccuracy() == map.getAccuracy() &&
				getStarDifficulty() == map.getStarDifficulty() &&
				getSuccessRate() == map.getSuccessRate() &&
				getThumbUrl().equals(map.getThumbUrl()) &&
				getTitle().equals(map.getTitle());
	}
    
	/*
    public JSONObject toJson(){
        return mapToJson(this);
    }
    
    public static JSONObject mapToJson(OsuBeatmap map){
        JSONObject json = new JSONObject();
        json.put("orgUrl", map.getOriginalUrl());
        json.put("artist", map.getArtist());
        json.put("badRating", map.getBadRating());
        json.put("bpm", map.getBpm());
        json.put("creator", map.getCreator());
        //json.put("diff", map.getDifficulties());
        json.put("dwnUrl", map.getDwnUrl());
        json.put("genre", map.getGenre());
        json.put("goodRating", map.getGoodRating());
        json.put("name", map.getName());
        json.put("rating", map.getRating());
        json.put("source", map.getSource());
        json.put("circleSize", map.getCircleSize());
        json.put("approachRate", map.getApproachRate());
        json.put("hpDrain", map.getHpDrain());
        json.put("accuracy", map.getAccuracy());
        json.put("starDiff", map.getStarDifficulty());
        json.put("successRate", map.getSuccessRate());
        json.put("thumbUrl", map.getThumbUrl());
        json.put("title", map.getTitle());
        return json;
    }
    
    public static OsuBeatmap fromJson(JSONObject json){
        return new OsuBeatmap(
                json.getString("orgUrl"), json.getString("name"), json.getString("title"),
                json.getString("artist"), json.getString("creator"), json.getString("source"),
                json.getString("genre"), json.getString("dwnUrl"), json.getString("thumbUrl"),
                (float) json.getDouble("circleSize"), (float) json.getDouble("approachRate"), (float) json.getDouble("hpDrain"),
                (float) json.getDouble("accuracy"), (float) json.getDouble("starDiff"), json.getInt("badRating"),
                json.getInt("goodRating"), (float) json.getDouble("bpm"), (float) json.getDouble("successRate"),
                true);
    }
    */

}
