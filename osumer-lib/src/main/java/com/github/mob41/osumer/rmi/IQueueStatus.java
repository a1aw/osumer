package com.github.mob41.osumer.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IQueueStatus extends Remote{
    
    public String getTitle() throws RemoteException;
    
    public String getFileName() throws RemoteException;
    
    public String getThumbUrl() throws RemoteException;
    
    public int getProgress() throws RemoteException;

    //Returns ETA in seconds
    public int getEta() throws RemoteException;

    //Returns Elapsed Time in seconds
    public int getElapsed() throws RemoteException;
    
    //Returns status constants
    public int getStatus() throws RemoteException;
    
}
