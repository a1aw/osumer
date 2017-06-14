package com.github.mob41.osumer.io;

import java.util.Observable;

public abstract class Downloader extends Observable implements Runnable {

    public static final int DOWNLOADING = 0;

    public static final int PAUSED = 1;

    public static final int COMPLETED = 2;

    public static final int CANCELLED = 3;

    public static final int ERROR = 4;

    public abstract String getDownloadFolder();

    public abstract String getFileName();

    public abstract String getUrl();

    public abstract int getDownloaded();

    public abstract int getSize();

    public abstract float getProgress();

    public abstract int getStatus();

    public abstract void pause();

    public abstract void resume();

    public abstract void cancel();

    public abstract void download();

    public abstract void run();

}
