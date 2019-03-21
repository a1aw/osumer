package com.github.mob41.osumer.daemon;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import com.github.mob41.osumer.queue.Queue;
import com.github.mob41.osumer.queue.QueueManager;

public interface IDaemon extends Remote{
    
    public boolean addQueue(String url) throws RemoteException;
    
    public boolean addQueue(String url, int downloadAction, String targetFileOrFolder) throws RemoteException;
    
    public void reloadConfiguration() throws RemoteException, IOException;
    
}
