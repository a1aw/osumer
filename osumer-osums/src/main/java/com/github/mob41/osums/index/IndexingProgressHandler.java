package com.github.mob41.osums.index;

public abstract class IndexingProgressHandler {
    
    public static final int STATUS_STOPPED = 0;
    
    public static final int STATUS_STARTED = 1;
    
    public static final int STATUS_ERROR = 2;
    
    public static final int STATUS_PAUSED = 3;
    
    public static final int STATUS_COMPLETED = 4;
    
    public static final int SEARCHING_MAPS = 0;
    
    public static final int DOWNLOADING_INFO = 1;
    
    private int beatmapIndexed = 0;

    private int totalPages = 0;
    
    private int completedPages = 0;
    
    private int mode = SEARCHING_MAPS;
    
    private int status = STATUS_STOPPED;
    
    public IndexingProgressHandler() {
        
    }
    
    protected final void setTotalPages(int totalPages){
        this.totalPages = totalPages;
    }
    
    public final int getTotalPages(){
        return totalPages;
    }
    
    protected final void setCompletedPages(int completedPages){
        this.completedPages = completedPages;
    }
    
    public final int getCompletedPages(){
        return completedPages;
    }

    protected final void setBeatmapIndexed(int beatmapIndexed){
        this.beatmapIndexed = beatmapIndexed;
    }
    
    public final int getBeatmapIndexed(){
        return beatmapIndexed;
    }
    
    protected final void setStatus(int status) {
        this.status = status;
    }

    public final int getStatus() {
        return status;
    }

    protected final void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }
    
    public abstract boolean onStart();
    
    public abstract boolean onPause();
    
    public abstract boolean onError();
    
    public abstract boolean onComplete();
    
    public abstract boolean onLoopStart();
    
    public abstract boolean onLoopEnd();
}
