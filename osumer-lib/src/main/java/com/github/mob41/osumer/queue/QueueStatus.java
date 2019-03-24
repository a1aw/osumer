package com.github.mob41.osumer.queue;

import java.io.Serializable;
import java.rmi.RemoteException;

public class QueueStatus implements Serializable{

    private String title;
    
    private String fileName;
    
    private String thumbUrl;
    
    private int progress;
    
    private long eta;
    
    private long elapsed;
    
    private int status;

    public QueueStatus(String title, String fileName, String thumbUrl, int progress, long eta, long elapsed, int status) {
        super();
        this.title = title;
        this.fileName = fileName;
        this.thumbUrl = thumbUrl;
        this.progress = progress;
        this.eta = eta;
        this.elapsed = elapsed;
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getEta() {
        return eta;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(int elapsed) {
        this.elapsed = elapsed;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
