package com.github.mob41.osumer;

public class OsumerNative {

    static {
        System.loadLibrary("osumer-jni");
    }
    
    public static native void startWithOverlay();
    
    public static native void injectOverlay();
    
    public static native String getProgramFiles();
    
}
