package com.github.mob41.osums.index;

public class IndexingProgressAdapter extends IndexingProgressHandler {

    public IndexingProgressAdapter() {
        
    }

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
