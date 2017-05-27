package com.github.mob41.osumer.sock;

import java.io.IOException;
import java.net.Socket;

public class ConnThread extends Thread{
    
    private final Socket socket;
    
    public ConnThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run(){
        
    }
    
}
