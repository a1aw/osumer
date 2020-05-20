package com.github.mob41.osums.search;

import com.github.mob41.osumer.debug.WithDumpException;
import com.github.mob41.osums.Osums;
import com.github.mob41.osums.beatmap.OsuSong;

public class SongResult {
	
	private static final String SONG_URL = "https://osu.ppy.sh/s/";
    
    private int id;
    
    private String rank;

    private String artist;
    
    private String title;
    
    private String creator;
    
    private String[] tags;
    
    private int favourites;
    
    private int plays;
    
    protected SongResult(){
        
    }
    
    public SongResult(int id, String rank, String artist, String title, String creator, String[] tags, int favourites, int plays) {
        this.id = id;
        this.rank = rank;
        this.artist = artist;
        this.title = title;
        this.creator = creator;
        this.tags = tags;
        this.favourites = favourites;
        this.plays = plays;
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
    
    public OsuSong getOsuSong(Osums osums) throws WithDumpException{
        return osums.getSongInfo(SONG_URL + id);
    }

    public int getId() {
        return id;
    }

	public String getRank() {
		return rank;
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
