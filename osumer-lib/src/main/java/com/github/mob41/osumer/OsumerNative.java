package com.github.mob41.osumer;

import java.io.File;

import javax.swing.JOptionPane;

public class OsumerNative {

    static {
    	final String dllName = "osumer-jni.dll";
        final String path32 = "C:\\Program Files\\osumer2\\" + dllName;
        final String path64 = "C:\\Program Files (x86)\\osumer2\\" + dllName;

        boolean success = false;
        String reason = null;

    	File file32 = new File(path32);
        File file64 = new File(path64);
        
        if (file64.exists()) {
        	try {
            	System.load(path64);
            	success = true;
        	} catch (UnsatisfiedLinkError e) {
        		e.printStackTrace();
        		reason = e.getMessage();
        	}
        } else if (file32.exists()) {
        	try {
            	System.load(path32);
            	success = true;
        	} catch (UnsatisfiedLinkError e) {
        		e.printStackTrace();
        		reason = e.getMessage();
        	}
        } else {
        	JOptionPane.showMessageDialog(null, "Could not find any osumer2 DLL in both 32, 64 bit folders!\nYou might not be able to use overlay features.\nPlease check if you have installed osumer2 properly.", "osumer2 Native Error", JOptionPane.ERROR_MESSAGE);
        	System.exit(-1);
        }
        
        if (!success) {
        	JOptionPane.showMessageDialog(null, "Could not load osumer2 DLL!\nPlease check if you have installed osumer2 properly.\nReason:\n" + reason, "osumer2 Native Error", JOptionPane.ERROR_MESSAGE);
        	System.exit(-1);
        }
    }
    
    public static native void startWithOverlay();
    
    public static native void injectOverlay();
    
    public static native String getProgramFiles();
    
}
