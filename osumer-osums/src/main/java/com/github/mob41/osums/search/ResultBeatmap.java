package com.github.mob41.osums.search;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.github.mob41.osumer.debug.WithDumpException;
import com.github.mob41.osums.Osums;
import com.github.mob41.osums.beatmap.BeatmapPage;

public class ResultBeatmap {
    
    private int id;

    private String artist;
    
    private String title;
    
    private String creator;
    
    private String[] tags;
    
    private int favourites;
    
    private int plays;
    
    private String beatmapUrl;
    
    private String thumbUrl;
    
    private String thumbData;
    
    protected ResultBeatmap(){
        
    }
    
    public ResultBeatmap(int id, String artist, String title, String creator, String[] tags, int favourites, int plays, String beatmapUrl, String thumbUrl, String thumbData) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.creator = creator;
        this.tags = tags;
        this.favourites = favourites;
        this.plays = plays;
        this.beatmapUrl = beatmapUrl;
        this.thumbUrl = thumbUrl;
        this.thumbData = thumbData;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }

    public String[] getTags() {
        return tags;
    }

    public int getFavourites() {
        return favourites;
    }

    public int getPlays() {
        return plays;
    }

    public String getBeatmapUrl() {
        return beatmapUrl;
    }
    
    public BeatmapPage getBeatmap(Osums osums) throws WithDumpException{
        return osums.getBeatmapInfo(beatmapUrl != null ? beatmapUrl : "https://osu.ppy.sh/s/" + id);
    }

    public int getId() {
        return id;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getThumbData() {
        return thumbData;
    }

    /*
    public JSONObject toJson(){
        JSONArray arr = new JSONArray();
        
        for (int i = 0; i < tags.length; i++){
            arr.put(tags[i]);
        }
        
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("artist", artist);
        json.put("title", title);
        json.put("creator", creator);
        json.put("tags", arr);
        json.put("favourites", favourites);
        json.put("plays", plays);
        json.put("beatmapUrl", beatmapUrl);
        json.put("thumbUrl", thumbUrl);
        json.put("thumbData", thumbData);
        return json;
    }
    
    public static ResultBeatmap fromJson(JSONObject json){
        JSONArray tagsArr = json.getJSONArray("tags");
        String[] tags = new String[tagsArr.length()];
        for (int i = 0; i < tags.length; i++){
            tags[i] = tagsArr.getString(i);
        }
        
        return new ResultBeatmap(
                json.getInt("id"), json.getString("artist"), json.getString("title"),
                json.getString("creator"), tags, json.getInt("favourites"), json.getInt("plays"),
                json.getString("beatmapUrl"), json.getString("thumbUrl"), json.getString("thumbData")
                );
    }
    */

}
