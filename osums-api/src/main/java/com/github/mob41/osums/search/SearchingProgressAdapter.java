package com.github.mob41.osums.search;

public class SearchingProgressAdapter extends SearchingProgressHandler {
    
    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onPause() {
        return true;
    }

    @Override
    public boolean onError() {
        return true;
    }

    @Override
    public boolean onComplete() {
        return true;
    }

    @Override
    public boolean onLoopStart() {
        return true;
    }

    @Override
    public boolean onLoopEnd() {
        return true;
    }

}
