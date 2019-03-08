package com.github.mob41.osumer.daemon;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;

public class DaemonServer extends Thread implements Closeable{

    public static final int DEFAULT_PORT = 46725;
    
    private ServerSocket ss = null;
    
    private boolean running = false;
    
    @Override
    public void run() {
        if (!running) {
            running = false;

            try {
                //TODO Dynamic Port
                ss = new ServerSocket(DEFAULT_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            running = true;
        }
    }

    public void close() throws IOException {
        
    }

}
