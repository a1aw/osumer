package com.github.mob41.osumer.daemon;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

public class DaemonClient implements Closeable{
    
    private final Socket socket;
    
    public DaemonClient(int port) throws IOException {
        this("127.0.0.1", port);
    }

    public DaemonClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
    }

    public void close() throws IOException {
        socket.close();
    }
}
