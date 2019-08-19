package com.github.mob41.osums.search;

public abstract class SearchingProgressHandler {
    
    public static final int STATUS_STOPPED = 0;
    
    public static final int STATUS_STARTED = 1;
    
    public static final int STATUS_ERROR = 2;
    
    public static final int STATUS_PAUSED = 3;
    
    public static final int STATUS_COMPLETED = 4;
    
    private int beatmapIndexed = 0;

    private int totalPages = 0;
    
    private int completedPages = 0;
    
    private int status = STATUS_STOPPED;
    
    public SearchingProgressHandler() {
        
    }
    
    public final void setTotalPages(int totalPages){
        this.totalPages = totalPages;
    }
    
    public final int getTotalPages(){
        return totalPages;
    }
    
    public final void setCompletedPages(int completedPages){
        this.completedPages = completedPages;
    }
    
    public final int getCompletedPages(){
        return completedPages;
    }

    public final void setBeatmapIndexed(int beatmapIndexed){
        this.beatmapIndexed = beatmapIndexed;
    }
    
    public final int getBeatmapIndexed(){
        return beatmapIndexed;
    }
    
    public final void setStatus(int status) {
        this.status = status;
    }

    public final int getStatus() {
        return status;
    }
    
    public abstract boolean onStart();
    
    public abstract boolean onPause();
    
    public abstract boolean onError();
    
    public abstract boolean onComplete();
    
    public abstract boolean onLoopStart();
    
    public abstract boolean onLoopEnd();

}
