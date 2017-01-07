package com.github.mob41.osumer.io;

import java.io.Serializable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.mob41.osumer.exceptions.DebuggableException;
import com.github.mob41.osumer.exceptions.OsuException;

public class OsuBeatmap implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5568409538053247272L;

	private final String name;
	
	private final String[] difficulties;
	
	private final String title;
	
	private final String artist;
	
	private final String creator;
	
	private final String source;
	
	private final String genre;
	
	private final String dwnUrl;
	
	private final String thumbUrl;
	
	private final int bad_rating;
	
	private final int good_rating;
	
	private final int bpm;
	
	private final float success_rate;

	private OsuBeatmap(String name, String[] difficulties, String title,
			String artist, String creator, String source, String genre,
			String dwnUrl, String thumbUrl, int badRating, int goodRating,
			int bpm, float successRate) {
		this.name = name;
		this.difficulties = difficulties;
		this.title = title;
		this.artist = artist;
		this.creator = creator;
		this.source = source;
		this.genre = genre;
		this.dwnUrl = dwnUrl;
		this.thumbUrl = thumbUrl;
		this.bad_rating = badRating;
		this.good_rating = goodRating;
		this.bpm = bpm;
		this.success_rate = successRate;
	}
	
	public String getName(){
		return name;
	}
	
	public String[] getDifficulties(){
		return difficulties;
	}
	
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

	public int getBadRating() {
		return bad_rating;
	}

	public int getGoodRating() {
		return good_rating;
	}
	
	public float getRating(){
		return (float) good_rating / (bad_rating + good_rating) * 100;
	}

	public int getBpm() {
		return bpm;
	}

	public float getSuccessRate() {
		return success_rate;
	}

	protected static OsuBeatmap createInstance(Document doc) throws DebuggableException{
		Elements dwnLinks = doc.getElementsByClass("beatmap_download_link");
		
		if (dwnLinks.size() == 0){
			throw new OsuException("No download link available. Invalid beatmap url?");
		}
		
		Element alnk = dwnLinks.get(0);
		
		String dwnLnk = alnk.attr("href");
		
		Element contentWithBgEl = doc.select("div.content-with-bg").first();
		
		if (contentWithBgEl == null){
			throw new OsuException("No content-with-bg in the content. Page broke?");
		}
		
		//Element header1 = contentWithBgEl.select("h1").first();
		
		Element tabList = contentWithBgEl.select("div#tablist ul").first();
		
		if (tabList == null){
			throw new OsuException("No list in the content. Page broke?");
		}
		
		Elements tabListLi = tabList.children();
		
		String[] tabStr = new String[tabListLi.size()];
		for (int i = 0; i < tabListLi.size(); i++){
			//<a> tag should be inside the <li> according to online code
			Element el = tabListLi.get(i).select(".beatmapTab span").first();
			
			if (el == null){
				throw new OsuException("No <a>/<span> tag in the difficulties tab index " + i + ". Page broke?");
			}
			
			tabStr[i] = el.html();
		}
		
		Element songInfo = contentWithBgEl.select("div table#songinfo tbody").first();
		
		if (songInfo == null){
			throw new OsuException("No song info in the content. Page broke?");
		}
		
		Elements songInfoTrs = songInfo.children();
		
		if (songInfoTrs.size() < 7){
			throw new OsuException("No song info table <tr> / not enough <tr>. Only " + songInfoTrs.size() + "/7 Page broke?");
		}
		
		//TODO: Rewrite here. Seems stupid these code XD
		String[] tags = null;
		String genre = null;
		String source = null;
		String creator = null;
		String artist = null;
		String title = null;
		int bpm = -1;
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
							throw new OsuException("No artist related data. Page broke?");
						}
						artist = el_a.html();
						break;
					case 2:
						break;
					case 3: //figuring to get
						break;
					case 4:
						break;
					case 5: //figuring to get
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
							throw new OsuException("No title related data. Page broke?");
						}
						title = el_a.html();
						break;
					case 2:
						break;
					case 3: //figuring to get
						break;
					case 4:
						break;
					case 5: //figuring to get
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
							throw new OsuException("No creator related data. Page broke?");
						}
						creator = el_a.html();
						break;
					case 2:
						break;
					case 3: //figuring to get
						break;
					case 4:
						break;
					case 5: //figuring to get
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
							throw new OsuException("No source related data. Page broke?");
						}
						source = el_a.html();
						break;
					case 2:
						break;
					case 3: //Format: xx (xx), however we are not getting (xx)
						el_a = el.select("a[href]").first();
						if (el_a == null){
							throw new OsuException("No genre related data. Page broke?");
						}
						genre = el_a.html();
						break;
					case 4:
						break;
					case 5: //Integer BPM
						if (el == null){
							throw new OsuException("No BPM related data. Page broke?");
						}
						try {
							bpm = Integer.parseInt(el.html().replaceAll(",", ""));
						} catch (NumberFormatException e){
							throw new OsuException("BPM is not integer", e);
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
							throw new OsuException("No tags related data. Page broke?");
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
							throw new OsuException("Not enough " + ratingEls.size() + "/2 / no rating related data. Page broke?");
						}
						el_a = ratingEls.get(0);
						try {
							bad_rating = Integer.parseInt(el_a.html().replaceAll(",", ""));
						} catch (NumberFormatException e){
							throw new OsuException("Bad rating is not integer", e);
						}
						el_a = ratingEls.get(1);
						try {
							good_rating = Integer.parseInt(el_a.html().replaceAll(",", ""));
						} catch (NumberFormatException e){
							throw new OsuException("Good rating is not integer", e);
						}
						break;
					case 4:
						break;
					case 5: //Integer BPM
						el_a = el.select("td").first();
						if (el_a != null && el_a.html().contains("Not yet played!")){
							success_rate = -1;
							break;
						}
						
						el_a = el.select("td b").first();
						if (el_a == null){
							throw new OsuException("No success rate related data. Page broke?");
						}
						try {
							String str = el_a.html();
							int index = str.indexOf("%");
							success_rate = Float.parseFloat(str.substring(0, index));
						} catch (NumberFormatException e){
							throw new OsuException("Success rate is not a floating number", e);
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
			throw new OsuException("No thumb image related data. Page broke?");
		}
		System.out.println("[" + thumbEl.attr("src") + "]THuB");
		
		String thumbUrl = thumbEl.attr("src");
		
		return new OsuBeatmap(artist + " - " + title,
				tabStr, title, artist, creator, source,
				genre, dwnLnk, thumbUrl, bad_rating,
				good_rating, bpm, success_rate);
	}

}
