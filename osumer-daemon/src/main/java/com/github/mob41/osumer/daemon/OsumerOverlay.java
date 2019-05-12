package com.github.mob41.osumer.daemon;

public class OsumerOverlay {

    static {
        System.loadLibrary("osumer-jni");
    }
    
    public static native void startWithOverlay();
    
    public static native void injectOverlay();
    
}
