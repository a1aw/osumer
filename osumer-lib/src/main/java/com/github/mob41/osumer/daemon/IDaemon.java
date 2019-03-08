package com.github.mob41.osumer.daemon;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.github.mob41.osumer.queue.Queue;

public interface IDaemon extends Remote{

    public void addQueue(Queue queue) throws RemoteException;
    
}
