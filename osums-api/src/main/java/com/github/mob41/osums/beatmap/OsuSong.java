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

public class OsuSong {

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
	
	private final int bad_rating;
	
	private final int good_rating;
	
	private final float bpm;
	
	private final boolean pageBeatmap;

	public OsuSong(String originalUrl, String name, String title,  //... String name, String[] difficulties ...
			String artist, String creator, String source, String genre, String dwnUrl,
			String thumbUrl, int badRating, int goodRating,
			float bpm, boolean pageBeatmap) {
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
		this.bad_rating = badRating;
		this.good_rating = goodRating;
		this.bpm = bpm;
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

    public String getOriginalUrl() {
        return originalUrl;
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
	
	public boolean equals(OsuSong map){
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
				getThumbUrl().equals(map.getThumbUrl()) &&
				getTitle().equals(map.getTitle());
	}

	public boolean isPageBeatmap() {
		return pageBeatmap;
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
