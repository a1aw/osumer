package com.github.mob41.osums.beatmap;

import com.github.mob41.osums.search.ResultBeatmap;

public abstract class BeatmapPage {

    public abstract String getTitle();
    
    public abstract String getArtist();
    
    public abstract String getCreator();

    public abstract String getSource();

    public abstract String getGenre();

    public abstract String getDwnUrl();

    public abstract String getThumbUrl();

    public abstract float getStarDifficulty();

    public abstract int getBadRating();

    public abstract int getGoodRating();
    
    public abstract float getRating();

    public abstract float getBpm();

    public abstract float getSuccessRate();

    public abstract String getOriginalUrl();
    
    public ResultBeatmap toResultBeatmap(){
        return new ResultBeatmap(-1, getArtist(), getTitle(), getCreator(), null, -1, -1, getOriginalUrl(), getThumbUrl(), null);
    }
}
