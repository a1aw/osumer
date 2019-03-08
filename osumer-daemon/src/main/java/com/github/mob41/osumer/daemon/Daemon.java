package com.github.mob41.osumer.daemon;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.github.mob41.osumer.queue.Queue;

public class Daemon extends UnicastRemoteObject implements IDaemon {

    /**
     * 
     */
    private static final long serialVersionUID = -7873474580122978651L;

    protected Daemon() throws RemoteException {
        super();
    }

    public void addQueue(Queue queue) throws RemoteException {
        // TODO Auto-generated method stub
        
    }

}
