package com.github.mob41.osumer.rmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import com.github.mob41.osumer.queue.Queue;
import com.github.mob41.osumer.queue.QueueManager;

public interface IUI extends Remote{
    
    public void onQueueStatusUpdate() throws RemoteException;
    
    public void wake() throws RemoteException;
    
}
